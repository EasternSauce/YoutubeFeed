package com.kamilk.ytfeed;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class representing single video and its data.
 */
class VideoData implements Serializable{
    /**
     * Youtube video title.
     */
    private String title;
    /**
     * Youtube video published date.
     */
    private DateTime publishedDate;
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


    private String duration;


    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getPublishedAt() {
        Date date = new Date(publishedDate.getValue());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    DateTime getPublishedDate() {
        return publishedDate;
    }

    void setPublishedDate(DateTime date) {
        this.publishedDate = date;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getUrl() {
        return "https://www.youtube.com/watch?v=" + getId();
    }

    String getThumbnailUrl() {
        return thumbnailUrl;
    }

    void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    String getChannelTitle() {
        return channelTitle;
    }

    void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    String getDuration() {
        return duration;
    }

    void setDuration(String duration) {
        this.duration = duration;
    }

}
