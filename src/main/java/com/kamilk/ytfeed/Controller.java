package com.kamilk.ytfeed;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by kamil on 2016-09-03.
 * Controller class of MVC pattern.
 */

class Controller {
    private Model model;
    private View view;
    private boolean inProgress;

    Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        inProgress = false;
    }


    void runApp() {
        handleListeners();
        model.loadFiles();
        view.displayMainWindow();

        Thread thread = new Thread() {
            public void run() {
                refreshVideos();
            }
        };
        thread.start();
    }

    private void handleListeners() {
        view.addUpdateButtonListener(new UpdateButtonListener());
        view.addChannelsButtonListener(new ChannelsButtonListener());
        view.addAddButtonListener(new AddButtonListener());
        view.addSearchButtonListener(new SearchButtonListener());
        view.addChannelsWindowListener(new ChannelsWindowListener());
    }

    private void refreshVideos() {
        if(inProgress) return;
        inProgress = true;


        view.showLoadingScreen();
        model.startPullingVideos();
        view.setProgressMax(model.getChannels().size() * 10);
        for (Channel channel : model.getChannels()) {

            model.pullVideos(channel);

            view.increaseProgress(7);
        }
        model.finishPullingVideos();

        view.startUpdatingVideos();

        for (Video video : model.getVideos()) {
            view.addVideoEntry(video, new VideoLinkListener(video));
            view.increaseProgress(3);
        }

        view.finishUpdatingVideos();

        view.resetProgress();

        inProgress = false;
    }

    private void refreshChannels() {
        view.startUpdatingChannels();
        for (Channel channel : model.getChannels()) {
            view.updateChannel(channel, new RemoveLinkListener(channel));
        }
        view.finishUpdatingChannels();
    }

    private void performChannelSearch() {
        List<Channel> results = model.queryChannels(view.getSearchedTerm());

        view.startSearchResults();
        for (Channel result : results) {
            view.showSearchResult(result, new AddLinkListener(result));
        }
        view.finishSearchResults();
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread thread = new Thread() {
                public void run() {
                    refreshVideos();
                }
            };
            thread.start();
        }
    }

    private class ChannelsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.startUpdatingChannels();
            for (Channel channel : model.getChannels()) {
                view.updateChannel(channel, new RemoveLinkListener(channel));
            }
            view.finishUpdatingChannels();
            view.displayChannelsWindow();
        }
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.showChannelSearch();
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
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
                        refreshVideos();
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
            refreshChannels();
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
            refreshChannels();
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
                Desktop.getDesktop().browse(new URI(video.getUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}