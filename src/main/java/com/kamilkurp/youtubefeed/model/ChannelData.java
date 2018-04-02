package com.kamilkurp.youtubefeed.model;



public class ChannelData {

    private final String id;

    private final String title;


    public ChannelData(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public ChannelData(ChannelData source) {
        this.id = source.getId();
        this.title = source.getTitle();
    }


    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
}

