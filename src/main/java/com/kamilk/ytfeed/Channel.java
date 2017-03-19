package com.kamilk.ytfeed;

/**
 * Youtube channel info.
 */
class Channel {
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
    Channel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    String getId() {
        return id;
    }
    String getTitle() {
        return title;
    }
}
