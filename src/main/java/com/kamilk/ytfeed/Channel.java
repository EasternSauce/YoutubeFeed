package com.kamilk.ytfeed;

/**
 * Created by kamil on 2016-09-02.
 * Youtube channel info.
 */

class Channel {
    private String id;
    private String title;

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
