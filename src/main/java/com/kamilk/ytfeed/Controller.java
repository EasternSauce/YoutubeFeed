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

/**
 * Controller class of MVC pattern.
 */
class Controller {
    /**
     * Model of MVC.
     */
    private final Model model;
    /**
     * View of MVC.
     */
    private final View view;
    /**
     * Used to prevent following updates from happening if the first one did not finish.
     */
    private boolean inProgress = false;

    /**
     * Constructor of controller.
     * @param model model of MVC
     * @param view view of MVC
     */
    Controller(final Model model, final View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Actually runs the MVC program.
     */
    void runApp() {
        handleListeners(); //add every listener

        model.loadFiles(); //load serialized data if found, also load channel list

        view.displayMainWindow();

        view.clearMainWindowVideosPanel();

        try {
            for (final Video video : model.getFeedVideos()) view.addMainWindowVideoEntry(video, new VideoLinkListener(video));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        view.updateMainWindowVideosPanel();

        //dont block input when updating
        final Thread thread = new Thread() {
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
        view.addMainWindowUpdateButtonListener(new UpdateButtonListener());
        view.addMainWindowChannelsButtonListener(new ChannelsButtonListener());
        view.addChannelsWindowAddButtonListener(new AddButtonListener());
        view.addSearchWindowSearchButtonListener(new SearchButtonListener());
        view.addChannelsWindowListener(new ChannelsWindowListener());
    }

    /**
     * Gets the videos pulled from Youtube API and displays them on the main window. Displays the loading screen and progress bar while updating.
     * @param since how old can the videos' published date be
     */
    private void pullAndShowVideos(final Date since) {
        view.showMainWindowLoadingScreen();

        final List<Channel> channels = model.getFeedChannels();
        view.setMainWindowProgressMax(channels.size() * 10);

        for (final Channel channel : channels) {
            try {
                model.pullVideosWithPuller(channel, since);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            view.increaseMainWindowProgress(7);
        }
        model.setFeedLastUpdated(new Date()); //set last updated to current time
        model.sortVideosInFeed();
        try {
            model.serializeFeedToCache();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        view.clearMainWindowVideosPanel();

        try {
            for (final Video video : model.getFeedVideos()) {
                view.addMainWindowVideoEntry(video, new VideoLinkListener(video));
                view.increaseMainWindowProgress(3);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        view.updateMainWindowVideosPanel();

        view.resetMainWindowProgress();
    }

    /**
     * Updates video list but not wholly (only since the last time it was updated). Its way quicker than refreshing the whole list.
     */
    private void updateVideoList() {
        if(inProgress) return;
        inProgress = true;

        pullAndShowVideos(model.getFeedLastUpdated());

        inProgress = false;
    }

    /**
     * Refreshes and reloads the video list completely. Takes a while.
     */
    private void refreshVideoList() {
        if(inProgress) return;
        inProgress = true;

        model.clearVideosFromFeed();

        final long DAY_IN_MS = 1000 * 60 * 60 * 24;
        pullAndShowVideos(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS))); //pull videos 7 days old at most

        inProgress = false;

    }

    /**
     * Reload and display the channels list.
     */
    private void refreshChannelList() {
        view.clearChannelsWindowChannelsPanel();
        for (final Channel channel : model.getFeedChannels()) {
            view.addChannelsWindowChannelEntry(channel, new RemoveLinkListener(channel));
        }
        view.updateChannelsWindowChannelsPanel();
    }

    /**
     * Reloads and displays search results.
     * @param results list of results
     */
    private void refreshSearchResults(final List<Channel> results) {
        view.clearSearchWindowSearchResults();
        for (final Channel result : results) {
            view.addSearchWindowResultEntry(result, new AddLinkListener(result));
        }
        view.updateSearchWindowSearchResults();
    }

    /**
     * Searches for videos and refreshes the results.
     */
    private void performChannelSearch() {
        final List<Channel> results;
        try {
            results = model.queryChannelsWithPuller(view.getSearchWindowSearchedTerm());
            refreshSearchResults(results);
        } catch (final IOException e) {
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
        public void actionPerformed(final ActionEvent e) {
            final Thread thread = new Thread() {
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
        public void actionPerformed(final ActionEvent e) {
            refreshChannelList();
            view.displayChannelsWindow();
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
        public void actionPerformed(final ActionEvent e) {
            view.showSearchWindowChannelSearch();
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
        public void actionPerformed(final ActionEvent e) {
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
        public void windowClosing(final WindowEvent e) {
            if (model.isFeedChanged()) {
                final Thread thread = new Thread() {
                    public void run() {
                        refreshVideoList();
                    }
                };
                thread.start();
                model.setFeedChanged(false);
            }

            try {
                final PrintWriter writer = new PrintWriter("channel_ids.txt", "UTF-8");
                for (final Channel channel : model.getFeedChannels()) {
                    writer.println(channel.getId());
                }
                writer.close();
            } catch (final FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (final UnsupportedEncodingException e1) {
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
        AddLinkListener(final Channel channel) {
            super();
            this.channel = channel;
        }

        /**
         * Acts on mouse clicks in the window. A workaround to update the channel list on every channel add.
         * @param e unused click event
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            model.addChannelToFeed(channel);

            //Thread thread = new Thread() {
                //public void run() {
                    refreshChannelList(); //is bg thread needed?? to be tested
                //}
            //};
            //thread.start();
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
        RemoveLinkListener(final Channel channel) {
            super();
            this.channel = channel;

        }

        /**
         * Update channel list on any click on the window. Workaround to not being able to update on remove.
         * @param e unused event
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            model.removeChannelFromFeed(channel);
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
        private final Video video;

        /**
         * Constructors remembers the video.
         * @param video remembered video
         */
        VideoLinkListener(final Video video) {
            this.video = video;
        }

        /**
         * Opens the link on click events.
         * @param event unused event
         */
        @Override
        public void mouseClicked(final MouseEvent event) {
            try {
                //noinspection Since15
                Desktop.getDesktop().browse(new URI(video.getUrl()));
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}