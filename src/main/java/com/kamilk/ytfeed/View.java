package com.kamilk.ytfeed;

import java.awt.event.*;
import java.io.IOException;

/**
 * Created by kamil on 2016-09-03.
 * View class of MVC pattern.
 */

class View {
    private MainWindow mainWindow;
    private ChannelsWindow channelsWindow;
    private SearchWindow searchWindow;

    View() {
        mainWindow = new MainWindow();
        channelsWindow = new ChannelsWindow();
        searchWindow = new SearchWindow();
    }

    void displayMainWindow() {
        mainWindow.display();
    }

    void addUpdateButtonListener(ActionListener actionListener) {
        mainWindow.addUpdateButtonListener(actionListener);
    }
    void addChannelsButtonListener(ActionListener actionListener) {
        mainWindow.addChannelsButtonListener(actionListener);
    }
    void addAddButtonListener(ActionListener actionListener) {
        channelsWindow.addAddButtonListener(actionListener);
    }
    void addSearchButtonListener(ActionListener actionListener) {
        searchWindow.addSearchButtonListener(actionListener);
    }
    void addChannelsWindowListener(WindowAdapter windowAdapter) {
        channelsWindow.addWindowListener(windowAdapter);
    }


    String getSearchedTerm() {
        return searchWindow.getSearchedTerm();
    }

    void displayChannelsWindow() {
        channelsWindow.setVisible(true);
    }

    void showLoadingScreen() {
        mainWindow.showLoadingScreen();
    }

    void startUpdatingVideos() {
        mainWindow.startUpdatingVideos();
    }

    void addVideoEntry(Video video, MouseAdapter mouseAdapter){
        try {
            mainWindow.addVideoEntry(video, mouseAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void finishUpdatingVideos() {
        mainWindow.finishUpdatingVideos();
    }

    void showChannelSearch() {
        searchWindow.setVisible(true);
    }

    void setProgressMax(int size) {
        mainWindow.setProgressMax(size);
    }
    void increaseProgress(int i) {
        mainWindow.increaseProgress(i);
    }
    void resetProgress() {
        mainWindow.resetProgress();
    }


    void startSearchResults() {
        searchWindow.startSearchResults();
    }
    void showSearchResult(Channel channel, MouseAdapter mouseAdapter) {
        searchWindow.showSearchResult(channel, mouseAdapter);
    }
    void finishSearchResults() {
        searchWindow.finishSearchResults();
    }

    void startUpdatingChannels() {
        channelsWindow.startUpdatingChannels();
    }

    void updateChannel(Channel channel, MouseAdapter mouseAdapter) {
        channelsWindow.updateChannel(channel, mouseAdapter);
    }

    void finishUpdatingChannels() {
        channelsWindow.finishUpdatingChannels();
    }

}
