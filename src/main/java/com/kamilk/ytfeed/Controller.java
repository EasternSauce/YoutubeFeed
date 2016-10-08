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
 * Created by kamil on 2016-09-03.
 * Controller class of MVC pattern.
 */

class Controller {
    private Model model;
    private View view;
    //used to prevent following updates from happening if the first one did not finish
    private boolean inProgress = false;

    Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    //actually run the mvc program
    void runApp() {
        handleListeners(); //add every listener

        model.loadFiles(); //load serialized data if found, also load channel list

        view.displayMainWindow();

        view.clearVideosPanel();

        for (Video video : model.getVideos()) view.addVideoEntry(video, new VideoLinkListener(video));

        view.updateVideosPanel();

        //dont block input when updating
        Thread thread = new Thread() {
            public void run() {
                updateVideoList();
            }
        };
        thread.start();
    }

    //add every JComponent listener thats needed
    private void handleListeners() {
        view.addUpdateButtonListener(new UpdateButtonListener());
        view.addChannelsButtonListener(new ChannelsButtonListener());
        view.addAddButtonListener(new AddButtonListener());
        view.addSearchButtonListener(new SearchButtonListener());
        view.addChannelsWindowListener(new ChannelsWindowListener());
    }

    private void pullAndShowVideos(Date since) {
        view.showLoadingScreen();

        List<Channel> channels = model.getChannels();
        view.setProgressMax(channels.size() * 10);

        for (Channel channel : channels) {
            model.pullVideos(channel, since);

            view.increaseProgress(7);
        }
        model.setLastUpdated(new Date()); //set last updated to current time
        model.sortVideos();
        model.serializeToCache();

        view.clearVideosPanel();

        for (Video video : model.getVideos()) {
            view.addVideoEntry(video, new VideoLinkListener(video));
            view.increaseProgress(3);
        }

        view.updateVideosPanel();

        view.resetProgress();
    }

    //updates video list but only since the last time it was updated
    private void updateVideoList() {
        if(inProgress) return;
        inProgress = true;

        pullAndShowVideos(model.getLastUpdated());

        inProgress = false;
    }

    //refreshes and reloads the video list completely
    private void refreshVideoList() {
        if(inProgress) return;
        inProgress = true;

        model.clearVideos();

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        pullAndShowVideos(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS))); //pull videos 7 days old at most

        inProgress = false;

    }


    private void refreshChannelList() {
        view.clearChannelsPanel();
        for (Channel channel : model.getChannels()) {
            view.addChannelEntry(channel, new RemoveLinkListener(channel));
        }
        view.updateChannelsPanel();
    }

    private void refreshSearchResults(List<Channel> results) {
        view.clearSearchResults();
        for (Channel result : results) {
            view.addResultEntry(result, new AddLinkListener(result));
        }
        view.updateSearchResults();
    }

    //searches for videos and refreshes the results
    private void performChannelSearch() {
        List<Channel> results = model.queryChannels(view.getSearchedTerm());
        refreshSearchResults(results);
    }

    private class UpdateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Thread thread = new Thread() {
                public void run() {
                    updateVideoList();
                }
            };
            thread.start();
        }
    }

    private class ChannelsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.clearChannelsPanel();
            for (Channel channel : model.getChannels()) {
                view.addChannelEntry(channel, new RemoveLinkListener(channel));
            }
            view.updateChannelsPanel();
            view.displayChannelsWindow();
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            view.showChannelSearch();
        }
    }

    private class SearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            performChannelSearch();
        }
    }

    private class ChannelsWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (model.isFeedChanged()) {
                Thread thread = new Thread() {
                    public void run() {
                        refreshVideoList();
                    }
                };
                thread.start();
                model.setFeedChanged(false);
            }

            try {
                PrintWriter writer = new PrintWriter("channel_ids.txt", "UTF-8");
                for (Channel channel : model.getChannels()) {
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

    private class AddLinkListener extends MouseAdapter {
        private Channel channel;

        AddLinkListener(Channel channel) {
            super();
            this.channel = channel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            model.addChannelToFeed(channel);

            Thread thread = new Thread() {
                public void run() {
                    refreshChannelList();
                }
            };
            thread.start();
        }
    }

    private class RemoveLinkListener extends MouseAdapter {
        private Channel channel;

        RemoveLinkListener(Channel channel) {
            super();
            this.channel = channel;

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            model.removeChannelFromFeed(channel);
            refreshChannelList();
        }
    }

    private class VideoLinkListener extends MouseAdapter {
        private Video video;

        VideoLinkListener(Video video) {
            this.video = video;
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            try {
                //noinspection Since15
                Desktop.getDesktop().browse(new URI(video.getUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}