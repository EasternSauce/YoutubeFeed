package com.kamilk.ytfeed;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.*;

/**
 * Pulling data using the Youtube API.
 */
class YoutubePuller {
    /**
     * Maximum number of result videos for channel.
     */
    private final long NUMBER_OF_RESULTS_RETURNED = 25;
    /**
     * Youtube instance.
     */
    private YouTube youtube;
    /**
     * Loaded API key.
     */
    private String apiKey;

    /**
     * Sets API credentials.
     * @param credentialName name of credential
     * @param appName name of app
     */
    void setCredentials(final String credentialName, final String appName) throws IOException {
        final List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        final Credential credential = Auth.authorize(scopes, credentialName);

        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                appName).build();

        apiKey = new Properties().getProperty("youtube.apikey");
    }

    /**
     * Gets videos since a date from a channel.
     * @param channelId channel to pull from
     * @param dateSince date since to pull
     * @return list of results
     */
    List<SearchResult> getVideosSince(final String channelId, final Date dateSince) throws IOException {
        final YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);
        search.setChannelId(channelId);
        search.setPublishedAfter(new DateTime(dateSince));

        search.setType("video");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED);

        final SearchListResponse searchResponse = search.execute();
        return searchResponse.getItems();
    }

    /**
     * Searches for channels.
     * @param query search keywords
     * @return list of search results
     */
    List<Channel> queryChannels(final String query) throws IOException {
        final List<Channel> results = new LinkedList<Channel>();

        final YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);

        search.setQ(query);
        search.setType("channel");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED);

        final SearchListResponse searchResponse = search.execute();
        final List<SearchResult> searchResultList = searchResponse.getItems();

        for (final SearchResult searchResult : searchResultList) {
            results.add(new Channel(searchResult.getSnippet().getChannelId(), searchResult.getSnippet().getChannelTitle()));
        }


        return results;

    }

    /**
     * Accesses the video's published date.
     * @param videoId video ID to get published date of
     * @return published date
     */
    DateTime getVideoPublishedDate(final String videoId) throws IOException {
        final VideoListResponse videoListResponse = youtube.videos().list("snippet").setId(videoId).execute();

        final List<com.google.api.services.youtube.model.Video> videoList = videoListResponse.getItems();

        final Video video = videoList.get(0);

        return video.getSnippet().getPublishedAt();

    }

    /**
     * Accesses the channel's title.
     * @param channelId channel ID to pull title of
     * @return channel title
     */
    String getChannelTitle(final String channelId) throws IOException {
        final YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);

        search.setChannelId(channelId);
        search.setType("channel");

        final SearchListResponse searchResponse = search.execute();
        final List<SearchResult> searchResultList = searchResponse.getItems();

        return searchResultList.get(0).getSnippet().getChannelTitle();
    }

}
