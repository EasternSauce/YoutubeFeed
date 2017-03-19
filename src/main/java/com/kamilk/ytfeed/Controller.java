package com.kamilk.ytfeed;

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
//    private final ErrorView errorView;
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


        try {
            feedModel.loadFiles(); //load serialized data if found, also load channel list
        } catch (CredentialsException e) {
            mainView.displayErrorDialogAndExit("Credentials error", "Enter Client ID and Secret from "
                    + "https://console.developers.google.com/project/_/apiui/credential into "
                    + "the main directory file client_secrets.json");
        } catch (IOException e) {
            mainView.displayErrorDialogAndExit("Loading error", "Input output error while loading files, "
                    + "make sure the directory .oauth-credentals had a file textDatastore");
        } catch (URISyntaxException e) {
            mainView.displayErrorDialogAndExit("Loading error", "URI syntax problem while loading files");
        } catch (ClassNotFoundException e) {
            mainView.displayErrorDialogAndExit("Loading error", "Class not found while loading files");
        }

        mainView.display();

            mainView.clearVideosPanel();

            try {
                for (VideoData video : feedModel.getVideos()) {
                    mainView.addVideoEntryToPanel(video, new VideoLinkListener(video));
                }
            }
            catch (IOException e) {
                mainView.displayErrorDialogAndExit("Video Entry Error", "Input output error while loading video entries");
            }

            mainView.updateVideosPanel();

        //dont block input when updating
        Thread thread = new Thread() {
            public void run() {
                try {
                    updateVideoList();
                } catch (IOException e) {
                    mainView.displayErrorDialogAndExit("Video List Update Error", "Input output error while updating video list");
                }
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

        mainView.addErrorDialogListener(new ErrorDialogListener());
        channelsView.addErrorDialogListener(new ErrorDialogListener());
        searchView.addErrorDialogListener(new ErrorDialogListener());
    }

    /**
     * Gets the videos pulled from Youtube API and displays them on the main window. Displays the loading screen and progress bar while updating.
     * @param since how old can the videos' published date be
     */
    private void pullAndShowVideos(Date since) throws IOException {
        mainView.showLoadingScreen();

        List<Channel> channels = feedModel.getChannels();
        mainView.setProgressMax(channels.size() * 10);

        for (Channel channel : channels) {
            feedModel.addVideos(channel, since);

            mainView.increaseProgress(7);
        }
        feedModel.setLastUpdated(new Date());
        feedModel.sortVideos();
        try {
            feedModel.serializeToCache("cache");
        } catch (IOException e) {
            mainView.displayErrorDialogAndExit("Serialization error", "Input output error while serializing cache");
        }

        mainView.clearVideosPanel();

        try {
            for (VideoData video : feedModel.getVideos()) {
                mainView.addVideoEntryToPanel(video, new VideoLinkListener(video));
                mainView.increaseProgress(3);
            }
        } catch (IOException e) {
            mainView.displayErrorDialogAndExit("Panel update error", "Input output error while adding video entries");
        }

        mainView.updateVideosPanel();

        mainView.resetProgress();
    }

    /**
     * Adds to already populated list videos uploaded since the last time it was updated. Its way quicker than refreshing the whole list.
     */
    private void updateVideoList() throws IOException {
        if(inProgress) return;
        inProgress = true;

        pullAndShowVideos(feedModel.getLastUpdated());

        inProgress = false;
    }

    /**
     * Refreshes and reloads the video list completely. Takes a while to complete.
     */
    private void refreshVideoList() {
        if(inProgress) return;
        inProgress = true;

        feedModel.clearVideos();

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        try {
            pullAndShowVideos(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS))); //pull videos 7 days old at most
        } catch (IOException e) {
            mainView.displayErrorDialogAndExit("Pulling videos problem", "Problem during pulling and showing videos");
        }

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
    private void performChannelSearch() throws IOException {
        List<Channel> results;
        results = feedModel.searchForChannels(searchView.getSearchedTerm());
        refreshSearchResults(results);


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
                    try {
                        updateVideoList();
                    } catch (IOException e1) {
                        mainView.displayErrorDialogAndExit("Updating videos error", "Input output error while updating the list after pushing the button");
                    }
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
            try {
                performChannelSearch();
            } catch (IOException e1) {
                mainView.displayErrorDialogAndExit("Channel search error", "Input output error while searching for channels");
            }
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

            PrintWriter writer = null;
            try {
                writer = new PrintWriter("channel_ids.txt", "UTF-8");
            } catch (FileNotFoundException e1) {
                mainView.displayErrorDialogAndExit("File not found error", "channel_ids.txt seems to be missing in program directory");
            } catch (UnsupportedEncodingException e1) {
                mainView.displayErrorDialogAndExit("Unsupported encoding error", "channel_ids.txt has unsupported encoding");
            }
            for (Channel channel : feedModel.getChannels()) {
                writer.println(channel.getId());
            }
            writer.close();
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
                mainView.displayErrorDialogAndExit("Url getting error", "Input output error while getting the url");
            } catch (URISyntaxException e) {
                mainView.displayErrorDialogAndExit("Url getting error", "Uri syntax error while getting the url");
            }

        }
    }

    private class ErrorDialogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(1);
        }
    }
}