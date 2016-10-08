package com.kamilk.ytfeed;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import java.io.*;
import java.util.*;

/**
 * Created by kamil on 2016-08-06.
 * Sorted feed of videos up to week old from selected channels.
 */

class Feed {
    private List<Video> videos = new LinkedList<Video>();
    private List<Channel> channels = new LinkedList<Channel>();

    private Date lastUpdated;
    private boolean changed = false;

    Date getLastUpdated() {
        return lastUpdated;
    }

    void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    boolean isChanged() {
        return changed;
    }

    void setChanged(boolean changed) {
        this.changed = changed;
    }

    //returns a copy of video list
    List<Video> getVideos() {
        List<Video> videosCopy = new LinkedList<Video>();

        for(Video video : videos) {
            Video videoCopy = new Video();

            videoCopy.setChannelTitle(video.getChannelTitle());
            videoCopy.setId(video.getId());
            videoCopy.setPublished(video.getPublished());
            videoCopy.setThumbnailUrl(video.getThumbnailUrl());
            videoCopy.setTitle(video.getTitle());

            videosCopy.add(videoCopy);
        }

        return videosCopy;
    }

    void addChannelId(String channelId, YoutubePuller query) throws IOException{
        Channel channel = new Channel(channelId, query.getChannelTitle(channelId));
        channels.add(channel);
    }

    List<Channel> getChannels() {
        List<Channel> channelsCopy = new LinkedList<Channel>();

        for(Channel channel : channels) {
            Channel channelCopy = new Channel(channel.getId(), channel.getTitle());
            channelsCopy.add(channelCopy);
        }

        return channelsCopy;
    }

    //pull the videos using the API and fill the video list
    void pullVideos(YoutubePuller query, Channel channel, Date since) throws IOException{

        List<SearchResult> searchResults = query.getVideosSince(channel.getId(), since);

        if(searchResults != null) {
            for (SearchResult searchResult : searchResults) {
                addVideo(searchResult, query);
            }
        }
    }

    void sortVideos() {
        Collections.sort(videos, new Comparator<Video>() {
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

    //add video from search result
    private void addVideo(SearchResult searchResult, YoutubePuller puller) throws IOException {
        ResourceId rId = searchResult.getId();

        if (rId.getKind().equals("youtube#video")) {
            Video vid = new Video();
            vid.setId(rId.getVideoId());
            vid.setTitle(searchResult.getSnippet().getTitle());
            vid.setChannelTitle(searchResult.getSnippet().getChannelTitle());
            vid.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getDefault().getUrl());

            vid.setPublished(puller.getVideoPublishedDate(vid.getId()));

            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            if(vid.getPublished().getValue() < System.currentTimeMillis() - (7 * DAY_IN_MS)) {
                return;
            }

            videos.add(vid);
        }
    }

    //add channel if not already added
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

    private void deleteCacheFiles(String directory) {

        File dir = new File(directory);
        File vidsDir = new File(directory + "/vids");

        File[] files = vidsDir.listFiles();

        if(files != null) {

            for (File file : files) {
                file.delete();
            }
        }

        files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if(file.isDirectory()) continue;
                file.delete();
            }
        }
    }

    void serializeToCache(String directory) throws IOException{

        File dir = new File(directory);
        File vidsDir = new File(directory + "/vids");

        dir.mkdir();
        vidsDir.mkdir();

        deleteCacheFiles(directory);

        int id = 0;

            for(Video video : videos) {
                    FileOutputStream fileOut =
                            new FileOutputStream(vidsDir.getPath() + "/video" + id++ + ".serializable");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(video);
                    out.close();
                    fileOut.close();

            }
            FileOutputStream fileOut =
                    new FileOutputStream(dir.getPath() + "/last_updated.serializable");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(getLastUpdated());
            out.close();
            fileOut.close();

    }

    void deserializeFromCache(String directory) throws IOException, ClassNotFoundException{
        File dir = new File(directory);

        File vidsDir = new File(directory + "/vids");

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            setLastUpdated(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)));
            return;
        }
        videos.clear();

            files = vidsDir.listFiles();
            if(files == null) throw new IOException();

            for (File file : files) {
                if(file.isDirectory()) continue;


                FileInputStream fileIn = new FileInputStream(file.getPath());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                videos.add((Video) in.readObject());
                in.close();
                fileIn.close();

            }
            FileInputStream fileIn = new FileInputStream(dir.getPath() + "/last_updated.serializable");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            setLastUpdated((Date) in.readObject());
            in.close();
            fileIn.close();

    }

    void clearVideos() {
        videos.clear();
    }

}
