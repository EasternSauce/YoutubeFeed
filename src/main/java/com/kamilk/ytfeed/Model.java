package com.kamilk.ytfeed;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Created by kamil on 2016-09-03.
 * Model class of MVC pattern. Mostly just delegates methods from Youtube puller and feed.
 */

class Model {
    private Feed feed;
    private YoutubePuller youtubePuller;

    Model() {
        feed = new Feed();
        try {
            youtubePuller = new YoutubePuller();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //load channels from file and serialized data from cache
    void loadFiles() {
        try {
            String line;

            InputStream fis = new FileInputStream("channel_ids.txt");

            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                feed.addChannelId(line, youtubePuller);
            }

            feed.deserializeFromCache("cache");

        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //delegations

    void pullVideos(Channel channel, Date since) {
        try {
            feed.pullVideos(youtubePuller, channel, since);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<Video> getVideos() {
        return feed.getVideos();
    }

    List<Channel> getChannels() {
        return feed.getChannels();
    }

    List<Channel> queryChannels(String term) {
        try {
            return youtubePuller.queryChannels(term);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void serializeToCache() {
        try {
            feed.serializeToCache("cache");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    void sortVideos() {
        feed.sortVideos();
    }
    boolean isFeedChanged() {
        return feed.isChanged();
    }

    void setFeedChanged(boolean changed) {
        feed.setChanged(changed);
    }

    void addChannelToFeed(Channel channel){
        feed.addChannel(channel);
    }

    void removeChannelFromFeed(Channel channel) {
        feed.removeChannel(channel);
    }

    Date getLastUpdated() {
        return feed.getLastUpdated();
    }

    void setLastUpdated(Date date) {
        feed.setLastUpdated(date);
    }

    void clearVideos() {
        feed.clearVideos();
    }

}
