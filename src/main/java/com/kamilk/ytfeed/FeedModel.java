package com.kamilk.ytfeed;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Sorted feed of videos up to week old from selected channels.
 */
class FeedModel {
    /**
     * List of videos to be displayed on main window.
     */
    private final List<VideoData> videos = new LinkedList<VideoData>();
    /**
     * List of channels to be displayed on channels window.
     */
    private final List<Channel> channels = new LinkedList<Channel>();

    /**
     * When was the feed last updated.
     */
    private Date lastUpdated;
    /**
     * Will any modification change the feed, so it needs udpating.
     */
    private boolean changed = false;

    private final Auth auth = new Auth();

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

    /**
     * Load channels from file and serialized data from cache.
     */
    void loadFiles() throws IOException, URISyntaxException, ClassNotFoundException, CredentialsException {
        auth.authorize();

        String line;

        File channelsFile = new File("channel_ids.txt");
        channelsFile.createNewFile();
        InputStream fis = new FileInputStream("channel_ids.txt");

        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            addChannelId(line);
        }

        deserializeFromCache("cache");


    }

    /**
     * Returns a copy of video list.
     */
    List<VideoData> getVideos() {
        List<VideoData> videosCopy = new LinkedList<VideoData>();

        for(VideoData video : videos) {
            VideoData videoCopy = new VideoData();

            videoCopy.setId(video.getId());
            videoCopy.setTitle(video.getTitle());
            videoCopy.setDuration(video.getDuration());
            videoCopy.setChannelTitle(video.getChannelTitle());
            videoCopy.setThumbnailUrl(video.getThumbnailUrl());
            videoCopy.setPublishedDate(video.getPublishedDate());

            videosCopy.add(videoCopy);
        }

        return videosCopy;
    }


    private void addChannelId(String channelId) throws IOException{
        Channel channel = auth.getChannel(channelId);
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



    /**
     * Simply sort the videos collection.
     */
    void sortVideos() {
        Collections.sort(videos, new Comparator<VideoData>() {
            public int compare(VideoData vid1, VideoData vid2) {
                long val1 = vid1.getPublishedDate().getValue();
                long val2 = vid2.getPublishedDate().getValue();
                if (val1 < val2) return -1;
                if (val1 == val2) return 0;
                return 1;
            }
        });
        Collections.reverse(videos);
    }


    void addVideos(Channel channel, Date since) throws IOException {

        List<VideoData> vids = auth.getVideosSince(channel.getId(), since);

        videos.addAll(vids);

    }

    /**
     * Adds channel if not already added.
     * @param channelToAdd channel to be added
     */
    void addChannel(Channel channelToAdd){
        Channel channelToAddCopy = new Channel(channelToAdd.getId(), channelToAdd.getTitle()); //defensive copy
        for(Channel channel : channels) {
            if(channelToAddCopy.getId().equals(channel.getId())) {
                return;
            }
        }
        channels.add(channelToAddCopy);
        changed = true;
    }

    /**
     * Removes the channel specified.
     * @param channelToRemove channel to be removed
     */
    void removeChannel(Channel channelToRemove) {
        Channel channelToRemoveCopy = new Channel(channelToRemove.getId(), channelToRemove.getTitle()); //defensive copy
        Iterator<Channel> i = channels.iterator();
        while (i.hasNext()) {
            Channel channel = i.next();
            if (channelToRemoveCopy.getId().equals(channel.getId())) {
                changed = true;
                i.remove();
                return;
            }
        }
    }

    List<Channel> searchForChannels(String query) throws IOException{
        return auth.searchForChannels(query);
    }

    /**
     * Delete cache directory completely from disk.
     * @param directory the cache directory
     */
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

    /**
     * Save videos cache to disk.
     * @param directory the cache directory
     */
    void serializeToCache(String directory) throws IOException{

        File dir = new File(directory);
        File vidsDir = new File(directory + "/vids");

        dir.mkdir();
        vidsDir.mkdir();

        deleteCacheFiles(directory);

        int id = 0;

            for(VideoData video : videos) {
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

    /**
     * Load videos cache from disk.
     * @param directory the cache directory
     */
    private void deserializeFromCache(String directory) throws IOException, ClassNotFoundException{
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
                videos.add((VideoData) in.readObject());
                in.close();
                fileIn.close();

            }
            FileInputStream fileIn = new FileInputStream(dir.getPath() + "/last_updated.serializable");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            setLastUpdated((Date) in.readObject());
            in.close();
            fileIn.close();

    }

    /**
     * Delegate the video list to be cleared.
     */
    void clearVideos() {
        videos.clear();
    }

}
