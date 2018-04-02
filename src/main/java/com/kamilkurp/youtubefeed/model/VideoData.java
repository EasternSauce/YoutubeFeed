package com.kamilkurp.youtubefeed.model;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoData implements Serializable{

    private String title;

    private DateTime publishedDate;

    private String id;

    private String thumbnailUrl;

    private String channelTitle;


    private String duration;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        Date date = new Date(publishedDate.getValue());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public DateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(DateTime date) {
        this.publishedDate = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return "https://www.youtube.com/watch?v=" + this.id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}

