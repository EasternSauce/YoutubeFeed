package com.kamilk.ytfeed;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import java.io.*;
import java.util.*;

/**
 * Sorted feed of videos up to week old from selected channels.
 */
class Feed {
    /**
     * List of videos to be displayed on main window.
     */
    private final List<Video> videos = new LinkedList<Video>();
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

    Date getLastUpdated() {
        return lastUpdated;
    }
    void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    boolean isChanged() {
        return changed;
    }
    void setChanged(final boolean changed) {
        this.changed = changed;
    }

    /**
     * Returns a copy of video list.
     */
    List<Video> getVideos() {
        final List<Video> videosCopy = new LinkedList<Video>();

        for(final Video video : videos) {
            final Video videoCopy = new Video();

            videoCopy.setChannelTitle(video.getChannelTitle());
            videoCopy.setId(video.getId());
            videoCopy.setPublished(video.getPublished());
            videoCopy.setThumbnailUrl(video.getThumbnailUrl());
            videoCopy.setTitle(video.getTitle());

            videosCopy.add(videoCopy);
        }

        return videosCopy;
    }

    /**
     * Add channel to feed.
     * @param channelId Youtube channel's ID
     * @param query needed to pull channel title
     */
    void addChannelId(final String channelId, final YoutubePuller query) throws IOException{
        final Channel channel = new Channel(channelId, query.getChannelTitle(channelId));
        channels.add(channel);
    }

    List<Channel> getChannels() {
        final List<Channel> channelsCopy = new LinkedList<Channel>();

        for(final Channel channel : channels) {
            final Channel channelCopy = new Channel(channel.getId(), channel.getTitle());
            channelsCopy.add(channelCopy);
        }

        return channelsCopy;
    }

    //

    /**
     * Pulls the videos using the API and fills the video list.
     * @param query pulling from the API
     * @param channel channel to pull videos for
     * @param since since when (published date) to pull videos
     */
    void pullVideos(final YoutubePuller query, final Channel channel, final Date since) throws IOException{

        final List<SearchResult> searchResults = query.getVideosSince(channel.getId(), since);

        if(searchResults != null) {
            for (final SearchResult searchResult : searchResults) {
                addVideo(searchResult, query);
            }
        }
    }

    /**
     * Simply sort the videos collection.
     */
    void sortVideos() {
        Collections.sort(videos, new Comparator<Video>() {
            public int compare(final Video vid1, final Video vid2) {
                final long val1 = vid1.getPublished().getValue();
                final long val2 = vid2.getPublished().getValue();
                if (val1 < val2) return -1;
                if (val1 == val2) return 0;
                return 1;
            }
        });
        Collections.reverse(videos);
    }

    /**
     * Adds video from search result.
     * @param searchResult the search result to add from
     * @param puller pulling from API
     */
    private void addVideo(final SearchResult searchResult, final YoutubePuller puller) throws IOException {
        final ResourceId rId = searchResult.getId();

        if (rId.getKind().equals("youtube#video")) {
            final Video vid = new Video();
            vid.setId(rId.getVideoId());
            vid.setTitle(searchResult.getSnippet().getTitle());
            vid.setChannelTitle(searchResult.getSnippet().getChannelTitle());
            vid.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getDefault().getUrl());

            vid.setPublished(puller.getVideoPublishedDate(vid.getId()));

            final long DAY_IN_MS = 1000 * 60 * 60 * 24;
            if(vid.getPublished().getValue() < System.currentTimeMillis() - (7 * DAY_IN_MS)) {
                return;
            }

            videos.add(vid);
        }
    }

    //

    /**
     * Adds channel if not already added.
     * @param channelToAdd channel to be added
     */
    void addChannel(final Channel channelToAdd){
        final Channel channelToAddCopy = new Channel(channelToAdd.getId(), channelToAdd.getTitle()); //defensive copy
        for(final Channel channel : channels) {
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
    void removeChannel(final Channel channelToRemove) {
        final Channel channelToRemoveCopy = new Channel(channelToRemove.getId(), channelToRemove.getTitle()); //defensive copy
        final Iterator<Channel> i = channels.iterator();
        while (i.hasNext()) {
            final Channel channel = i.next();
            if (channelToRemoveCopy.getId().equals(channel.getId())) {
                changed = true;
                i.remove();
                return;
            }
        }
    }

    /**
     * Delete cache directory completely from disk.
     * @param directory the cache directory
     */
    private void deleteCacheFiles(final String directory) {

        final File dir = new File(directory);
        final File vidsDir = new File(directory + "/vids");

        File[] files = vidsDir.listFiles();

        if(files != null) {

            for (final File file : files) {
                file.delete();
            }
        }

        files = dir.listFiles();
        if(files != null) {
            for (final File file : files) {
                if(file.isDirectory()) continue;
                file.delete();
            }
        }
    }

    /**
     * Save videos cache to disk.
     * @param directory the cache directory
     */
    void serializeToCache(final String directory) throws IOException{

        final File dir = new File(directory);
        final File vidsDir = new File(directory + "/vids");

        dir.mkdir();
        vidsDir.mkdir();

        deleteCacheFiles(directory);

        int id = 0;

            for(final Video video : videos) {
                    final FileOutputStream fileOut =
                            new FileOutputStream(vidsDir.getPath() + "/video" + id++ + ".serializable");
                    final ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(video);
                    out.close();
                    fileOut.close();

            }
            final FileOutputStream fileOut =
                    new FileOutputStream(dir.getPath() + "/last_updated.serializable");
            final ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(getLastUpdated());
            out.close();
            fileOut.close();

    }

    /**
     * Load videos cache from disk.
     * @param directory the cache directory
     */
    void deserializeFromCache(final String directory) throws IOException, ClassNotFoundException{
        final File dir = new File(directory);

        final File vidsDir = new File(directory + "/vids");

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            final long DAY_IN_MS = 1000 * 60 * 60 * 24;
            setLastUpdated(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)));
            return;
        }
        videos.clear();

            files = vidsDir.listFiles();
            if(files == null) throw new IOException();

            for (final File file : files) {
                if(file.isDirectory()) continue;


                final FileInputStream fileIn = new FileInputStream(file.getPath());
                final ObjectInputStream in = new ObjectInputStream(fileIn);
                videos.add((Video) in.readObject());
                in.close();
                fileIn.close();

            }
            final FileInputStream fileIn = new FileInputStream(dir.getPath() + "/last_updated.serializable");
            final ObjectInputStream in = new ObjectInputStream(fileIn);
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
