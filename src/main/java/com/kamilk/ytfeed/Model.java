package com.kamilk.ytfeed;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Model class of MVC pattern. Mostly just delegates methods from Youtube puller and feed.
 */
class Model {
    /**
     * Holds feed instance.
     */
    private final Feed feed = new Feed();
    /**
     * Holds Youtube puller instance.
     */
    final private YoutubePuller youtubePuller = new YoutubePuller();

    /**
     * Load channels from file and serialized data from cache.
     */
    void loadFiles() {
        try {
            youtubePuller.setCredentials("youtubepuller", "youtube-feed");

            String line;

            final InputStream fis = new FileInputStream("channel_ids.txt");

            final InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            final BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                feed.addChannelId(line, youtubePuller);
            }

            feed.deserializeFromCache("cache");

        } catch(final IOException e) {
            e.printStackTrace();
        } catch(final ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //////DELEGATED:
    void pullVideosWithPuller(final Channel channel, final Date since) throws IOException { feed.pullVideos(youtubePuller, channel, since); }
    List<Video> getFeedVideos() { return feed.getVideos(); }
    List<Channel> getFeedChannels() { return feed.getChannels(); }
    List<Channel> queryChannelsWithPuller(final String term) throws IOException { return youtubePuller.queryChannels(term); }
    void serializeFeedToCache() throws IOException { feed.serializeToCache("cache"); }
    void sortVideosInFeed() { feed.sortVideos(); }
    boolean isFeedChanged() { return feed.isChanged(); }
    void setFeedChanged(final boolean changed) { feed.setChanged(changed); }
    void addChannelToFeed(final Channel channel){ feed.addChannel(channel); }
    void removeChannelFromFeed(final Channel channel) { feed.removeChannel(channel); }
    Date getFeedLastUpdated() { return feed.getLastUpdated(); }
    void setFeedLastUpdated(final Date date) { feed.setLastUpdated(date); }
    void clearVideosFromFeed() { feed.clearVideos(); }

}
