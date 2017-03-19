package com.kamilk.ytfeed;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import static java.awt.Desktop.*;

/**
 * Controller class of MVC pattern.
 */
class Controller {
    /**
     * Model of MVC.
     */
    private final FeedModel feedModel;
    /**
     * View of MVC.
     */
    private final MainView mainView;
    private final ChannelsView channelsView;
    private final SearchView searchView;
    /**
     * Used to prevent following updates from happening if the first one did not finish.
     */
    private boolean inProgress = false;

    /**
     * Constructor of controller.
     * @param model feedModel of MVC
     */
    Controller(FeedModel model, MainView mainView, ChannelsView channelsView, SearchView searchView) {
        this.feedModel = model;
        this.mainView = mainView;
        this.channelsView = channelsView;
        this.searchView = searchView;

    }

    /**
     * Actually runs the MVC program.
     */
    void runApp() {
        handleListeners(); //add every listener

        feedModel.loadFiles(); //load serialized data if found, also load channel list

        mainView.display();

        mainView.clearVideosPanel();

        try {
            for (VideoData video : feedModel.getVideos()) mainView.addVideoEntryToPanel(video, new VideoLinkListener(video));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainView.updateVideosPanel();

        //dont block input when updating
        Thread thread = new Thread() {
            public void run() {
                updateVideoList();
            }
        };
        thread.start();
    }

    /**
     * Adds every component listener thats needed.
     */
    private void handleListeners() {
        mainView.addUpdateButtonListener(new UpdateButtonListener());
        mainView.addChannelsButtonListener(new ChannelsButtonListener());
        channelsView.addAddButtonListener(new AddButtonListener());
        searchView.addSearchButtonListener(new SearchButtonListener());
        channelsView.addWindowListener(new ChannelsWindowListener());
    }

    /**
     * Gets the videos pulled from Youtube API and displays them on the main window. Displays the loading screen and progress bar while updating.
     * @param since how old can the videos' published date be
     */
    private void pullAndShowVideos(Date since) {
        mainView.showLoadingScreen();

        List<Channel> channels = feedModel.getChannels();
        mainView.setProgressMax(channels.size() * 10);

        for (Channel channel : channels) {
            try {
                feedModel.addVideos(channel, since);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainView.increaseProgress(7);
        }
        feedModel.setLastUpdated(new Date());
        feedModel.sortVideos();
        try {
            feedModel.serializeToCache("cache");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainView.clearVideosPanel();

        try {
            for (VideoData video : feedModel.getVideos()) {
                mainView.addVideoEntryToPanel(video, new VideoLinkListener(video));
                mainView.increaseProgress(3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainView.updateVideosPanel();

        mainView.resetProgress();
    }

    /**
     * Updates video list but not wholly (only since the last time it was updated). Its way quicker than refreshing the whole list.
     */
    private void updateVideoList() {
        if(inProgress) return;
        inProgress = true;

        pullAndShowVideos(feedModel.getLastUpdated());

        inProgress = false;
    }

    /**
     * Refreshes and reloads the video list completely. Takes a while.
     */
    private void refreshVideoList() {
        if(inProgress) return;
        inProgress = true;

        feedModel.clearVideos();

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        pullAndShowVideos(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS))); //pull videos 7 days old at most

        inProgress = false;

    }

    /**
     * Reload and display the channels list.
     */
    private void refreshChannelList() {
        channelsView.clearChannelsPanel();
        for (Channel channel : feedModel.getChannels()) {
            channelsView.addChannelEntry(channel, new RemoveLinkListener(channel));
        }
        channelsView.updateChannelsPanel();
    }

    /**
     * Reloads and displays search results.
     * @param results list of results
     */
    private void refreshSearchResults(List<Channel> results) {
        searchView.clearChannelsPanel();
        for (Channel result : results) {
            searchView.addResultEntry(result, new AddLinkListener(result));
        }
        searchView.updateSearchResults();
    }

    /**
     * Searches for videos and refreshes the results.
     */
    private void performChannelSearch() {
        List<Channel> results;
        try {
            results = feedModel.searchForChannels(searchView.getSearchedTerm());
            refreshSearchResults(results);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Listener implementation to update button on main window
     */
    private class UpdateButtonListener implements ActionListener {
        /**
         * Listener action to update button on main window. Starts a background thread so as not to block the main thread with the update.
         * @param e unused listener event
         */
        public void actionPerformed(ActionEvent e) {
            Thread thread = new Thread() {
                public void run() {
                    updateVideoList();
                }
            };
            thread.start();
        }
    }

    /**
     * Listener implementation to update button on channels window.
     */
    private class ChannelsButtonListener implements ActionListener {
        /**
         * Listener action to update button on channels window. Displays the window afterwards.
         * @param e unused listener event
         */
        public void actionPerformed(ActionEvent e) {
            refreshChannelList();
            channelsView.display();
        }
    }

    /**
     * Add button on the channels window listener class.
     */
    private class AddButtonListener implements ActionListener {
        /**
         * Shows search window.
         * @param e unused listener event
         */
        public void actionPerformed(ActionEvent e) {
            searchView.setVisible(true);
        }
    }

    /**
     * Search button on the search window listener class.
     */
    private class SearchButtonListener implements ActionListener {
        /**
         * Perform search based on the keywords in the search box input.
         * @param e unused listener event
         */
        public void actionPerformed(ActionEvent e) {
            performChannelSearch();
        }
    }

    /**
     * Channel window events class.
     */
    private class ChannelsWindowListener extends WindowAdapter {

        /**
         * Act on window close. If the feed will be changed, starts a new thread that updates video list. Writes channel IDs to file.
         */
        @Override
        public void windowClosing(WindowEvent e) {
            if (feedModel.isChanged()) {
                Thread thread = new Thread() {
                    public void run() {
                        refreshVideoList();
                    }
                };
                thread.start();
                feedModel.setChanged(false);
            }

            try {
                PrintWriter writer = new PrintWriter("channel_ids.txt", "UTF-8");
                for (Channel channel : feedModel.getChannels()) {
                    writer.println(channel.getId());
                }
                writer.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Listener class for channel links displayed on main window on every video info.
     */
    private class AddLinkListener extends MouseAdapter {
        /**
         * Remembered channel.
         */
        private final Channel channel;

        /**
         * Constructor remembers the channel it was used for.
         * @param channel link depends on the channel ID, of course
         */
        AddLinkListener(Channel channel) {
            super();
            this.channel = channel;
        }

        /**
         * Acts on mouse clicks in the window. A workaround to update the channel list on every channel add.
         * @param e unused click event
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            feedModel.addChannel(channel);

            refreshChannelList();

        }
    }

    /**
     * 'Remove channel link on channel list window' listener.
     */
    private class RemoveLinkListener extends MouseAdapter {
        /**
         * Channel info.
         */
        private final Channel channel;

        /**
         * Constructor additionally remembers the channel.
         * @param channel the removed channel
         */
        RemoveLinkListener(Channel channel) {
            super();
            this.channel = channel;

        }

        /**
         * Update channel list on any click on the window. Workaround to not being able to update on remove.
         * @param e unused event
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            feedModel.removeChannel(channel);
            refreshChannelList();
        }
    }

    /**
     * Listener to click events on every video click area.
     */
    private class VideoLinkListener extends MouseAdapter {
        /**
         * The clickable video.
         */
        private final VideoData video;

        /**
         * Constructors remembers the video.
         * @param video remembered video
         */
        VideoLinkListener(VideoData video) {
            this.video = video;
        }

        /**
         * Opens the link on click events.
         * @param event unused event
         */
        @Override
        public void mouseClicked(MouseEvent event) {
            try {
                getDesktop().browse(new URI(video.getUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}