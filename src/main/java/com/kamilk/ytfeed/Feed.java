package com.kamilk.ytfeed;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.*;

/**
 * Created by kamil on 2016-08-06.
 * Sorted feed of videos up to week old from selected channels.
 */

class Feed {
    private List<Video> videos;
    private List<Channel> channels;
    private Boolean changed;

    Boolean isChanged() {
        return changed;
    }

    void setChanged(Boolean changed) {
        this.changed = changed;
    }

    Feed() {
        videos = new LinkedList<Video>();
        channels = new LinkedList<Channel>();
        changed = false;
    }

    List<Video> getVideos() {
        return videos;
    }

    void addChannelId(String channelId, YoutubePuller query) throws IOException{
        Channel channel = new Channel(channelId, query.getChannelTitle(channelId));
        channels.add(channel);
    }

    List<Channel> getChannels() {
        return channels;
    }

    void startPullingVideos() {
        videos.clear();
    }

    void pullVideos(YoutubePuller query, Channel channel) throws IOException{

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        List<SearchResult> searchResults = query.getVideosSince(channel.getId(), new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)));

        if(searchResults != null) {
            for (SearchResult searchResult : searchResults) {
                addVideo(searchResult, query);
            }
        }
    }

    void finishPullingVideos() {
        sort(videos);
    }

    private void sort(List<Video> videos) {
        Collections.sort(videos, new Comparator<Video>() {
            @Override
            public int compare(Video vid1, Video vid2) {
                long val1 = vid1.getPublished().getValue();
                long val2 = vid2.getPublished().getValue();
                if (val1 < val2) return -1;
                if (val1 == val2) return 0;
                return 1;
            }
        });
        Collections.reverse(videos);
    }

    private void addVideo(SearchResult searchResult, YoutubePuller puller) throws IOException {
        ResourceId rId = searchResult.getId();

        if (rId.getKind().equals("youtube#video")) {
            Video vid = new Video();
            vid.setId(rId.getVideoId());
            vid.setTitle(searchResult.getSnippet().getTitle());
            vid.setChannelTitle(searchResult.getSnippet().getChannelTitle());
            vid.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getDefault().getUrl());

            vid.setPublished(puller.getVideoPublishedDate(vid.getId()));

            videos.add(vid);
        }
    }

    void addChannel(Channel channelToAdd){
        for(Channel channel : channels) {
            if(channelToAdd.getId().equals(channel.getId())) {
                return;
            }
        }
        channels.add(channelToAdd);
        changed = true;
    }

    void removeChannel(Channel channelToRemove) {
        Iterator<Channel> i = channels.iterator();
        while (i.hasNext()) {
            Channel channel = i.next();
            if (channelToRemove.getId().equals(channel.getId())) {
                changed = true;
                i.remove();
                return;
            }
        }
    }

}
