package com.kamilkurp.youtubefeed.controller;

import com.kamilkurp.youtubefeed.model.FeedModel;
import com.kamilkurp.youtubefeed.view.ChannelsView;
import com.kamilkurp.youtubefeed.view.MainView;
import com.kamilkurp.youtubefeed.view.SearchView;

/**
 * Initialization of MVC and running app.
 */
public class App {
    /**
     * Main class of the app.
     * @param args no arguments needed or used
     */
    public static void main(String[] args) {
        Controller controller = new Controller(new FeedModel(), new MainView(), new ChannelsView(), new SearchView());
        controller.runApp();
    }
}
