package com.kamilk.ytfeed;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

class Auth {
    final private HttpTransport httpTransport = new NetHttpTransport();
    final private JsonFactory jsonFactory = new JacksonFactory();
    private YouTube youtube;
    final private String apiKey = new Properties().getProperty("youtube.apikey");
    private final long NUMBER_OF_RESULTS_RETURNED = 7;

    Auth() {
        Logger buggyLogger = java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName());
        buggyLogger.setLevel(java.util.logging.Level.SEVERE);
    }


    void authorize() throws URISyntaxException, CredentialsException, IOException {
        Reader clientSecretReader = new InputStreamReader(Auth.class.getClassLoader()
                .getResourceAsStream("client_secrets.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, clientSecretReader);

        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            throw new CredentialsException();

        }

        String credentialDir = ".oauth-credentials";
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(credentialDir));

        String credentialDatastore = "testDatastore";
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes).setCredentialDataStore(datastore)
                .build();

        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");

        String appName = "test";
        youtube = new YouTube.Builder(httpTransport, jsonFactory, credential).setApplicationName(appName).build();
    }


    private VideoData getVideoData(String videoId) throws IOException {
        VideoData videoData = new VideoData();

        String listParams = "snippet,contentDetails";
        YouTube.Videos query = youtube.videos();
        VideoListResponse videoListResponse = query.list(listParams).setId(videoId).execute();

        List<Video> videoList = videoListResponse.getItems();

        Video video = videoList.get(0);

        String title = video.getSnippet().getTitle();
        String duration = video.getContentDetails().getDuration().substring(2);
        String channelTitle = video.getSnippet().getChannelTitle();
        String thumbnailUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
        DateTime publishedDate = video.getSnippet().getPublishedAt();

        videoData.setId(videoId);
        videoData.setTitle(title);
        videoData.setDuration(duration);
        videoData.setChannelTitle(channelTitle);
        videoData.setThumbnailUrl(thumbnailUrl);
        videoData.setPublishedDate(publishedDate);

        return videoData;
    }

    List<VideoData> getVideosSince(String channelId, Date dateSince) throws IOException {
        List<VideoData> videos = new LinkedList<VideoData>();

        YouTube.Search.List search = youtube.search().list("id");

        search.setKey(apiKey);
        search.setChannelId(channelId);
        search.setPublishedAfter(new DateTime(dateSince));

        search.setType("video");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED);

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> results = searchResponse.getItems();

        for(SearchResult result : results) {
            ResourceId rId = result.getId();

            if (rId.getKind().equals("youtube#video")) {
                VideoData vid = new VideoData();
                vid.setId(rId.getVideoId());
                VideoData videoData = getVideoData(rId.getVideoId());
                videos.add(videoData);
            }
        }

        return videos;
    }

    List<Channel> searchForChannels(String query) throws IOException {
        List<Channel> results = new LinkedList<Channel>();

        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(apiKey);

        search.setQ(query);
        search.setType("channel");

        search.setMaxResults(NUMBER_OF_RESULTS_RETURNED * 2);

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        for (SearchResult searchResult : searchResultList) {
            results.add(new Channel(searchResult.getSnippet().getChannelId(), searchResult.getSnippet()
                    .getChannelTitle()));
        }


        return results;
    }

    Channel getChannel(String channelId) throws IOException {
        YouTube.Search.List search = youtube.search().list("snippet");

        search.setKey(apiKey);

        search.setChannelId(channelId);
        search.setType("channel");

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        return new Channel(channelId, searchResultList.get(0).getSnippet().getChannelTitle());
    }
}
