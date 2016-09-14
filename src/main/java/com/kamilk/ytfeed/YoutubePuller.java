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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by kamil on 2016-07-27.
 * Pulling data using the Youtube API.
 */

class YoutubePuller {
    private final long NUMBER_OF_RESULTS_RETURNED = 25;
    private YouTube youtube;
    private String apiKey;

    YoutubePuller() throws IOException {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        Credential credential = Auth.authorize(scopes, "youtubequery");

        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                "test").build();


        apiKey = new Properties().getProperty("youtube.apikey");
    }

    List<SearchResult> getVideosSince(String channelId, Date dateSince) throws IOException {
        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);
        search.setChannelId(channelId);
        search.setPublishedAfter(new DateTime(dateSince));

        search.setType("video");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED);

        SearchListResponse searchResponse = search.execute();
        return searchResponse.getItems();
    }

    List<Channel> queryChannels(String query) throws IOException {
        List<Channel> results = new ArrayList<Channel>();

        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);

        search.setQ(query);
        search.setType("channel");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED);

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        for (SearchResult searchResult : searchResultList) {
            results.add(new Channel(searchResult.getSnippet().getChannelId(), searchResult.getSnippet().getChannelTitle()));
        }


        return results;

    }

    DateTime getVideoPublishedDate(String videoId) throws IOException {
        VideoListResponse videoListResponse = youtube.videos().list("snippet").setId(videoId).execute();

        List<com.google.api.services.youtube.model.Video> videoList = videoListResponse.getItems();

        Video video = videoList.get(0);

        return video.getSnippet().getPublishedAt();

    }

    String getChannelTitle(String channelId) throws IOException {
        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);

        search.setChannelId(channelId);
        search.setType("channel");

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        return searchResultList.get(0).getSnippet().getChannelTitle();
    }

}
