package com.kamilk.ytfeed;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kamil on 2016-08-05.
 * Class representing single video and its data.
 */


class Video implements Serializable{
    private String title;
    private DateTime published;
    private String id;
    private String thumbnailUrl;
    private String channelTitle;

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    DateTime getPublished() {
        return published;
    }

    void setPublished(DateTime published) {
        this.published = published;
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

    String getPrettyDate() {
        Date date = new Date(published.getValue());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Warsaw"));
        return format.format(date);
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
}
