package com.kamilk.ytfeed;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by kamil on 2016-09-03.
 * Model class of MVC pattern.
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

    void loadFiles() {
        try {
            String line;

            InputStream fis = new FileInputStream("channel_ids.txt");

            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                feed.addChannelId(line, youtubePuller);
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
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

    void startPullingVideos() {
        feed.startPullingVideos();
    }

    void pullVideos(Channel channel) {
        try {
            feed.pullVideos(youtubePuller, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void finishPullingVideos() {
        feed.finishPullingVideos();
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

}
