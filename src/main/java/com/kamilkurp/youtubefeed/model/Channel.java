package com.kamilkurp.youtubefeed.model;

/**
 * Youtube channel info.
 */
public class Channel {
    /**
     * Youtube channel's ID.
     */
    private final String id;
    /**
     * Youtube channel's title.
     */
    private final String title;

    /**
     * Constructor initializing both parameters.
     * @param id Youtube channel's ID
     * @param title Youtube channel's title
     */
    public Channel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
}
