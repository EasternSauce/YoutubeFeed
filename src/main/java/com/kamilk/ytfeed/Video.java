package com.kamilk.ytfeed;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class representing single video and its data.
 */
class Video implements Serializable{
    /**
     * Youtube video title.
     */
    private String title;
    /**
     * Youtube video published date.
     */
    private DateTime published;
    /**
     * Youtube video ID.
     */
    private String id;
    /**
     * Youtube video thumbnail URL.
     */
    private String thumbnailUrl;
    /**
     * Youtube video channel title.
     */
    private String channelTitle;

    String getTitle() {
        return title;
    }

    void setTitle(final String title) {
        this.title = title;
    }

    DateTime getPublished() {
        return published;
    }

    void setPublished(final DateTime published) {
        this.published = published;
    }

    String getId() {
        return id;
    }

    void setId(final String id) {
        this.id = id;
    }

    String getUrl() {
        return "https://www.youtube.com/watch?v=" + getId();
    }

    String getPrettyDate() {
        final Date date = new Date(published.getValue());
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("Warsaw")); //not needed apparently because i already get the time of my timezone
        return format.format(date);
    }

    String getThumbnailUrl() {
        return thumbnailUrl;
    }

    void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    String getChannelTitle() {
        return channelTitle;
    }

    void setChannelTitle(final String channelTitle) {
        this.channelTitle = channelTitle;
    }
}
