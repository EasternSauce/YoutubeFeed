package com.kamilk.ytfeed;

import java.awt.event.*;
import java.io.IOException;

/**
 * Created by kamil on 2016-09-03.
 * View class of MVC pattern. Mostly just delegation from window instances.
 */

class View {

    //windows inheriting JFrame

    private MainWindow mainWindow;
    private ChannelsWindow channelsWindow;
    private SearchWindow searchWindow;

    View() {
        mainWindow = new MainWindow();
        channelsWindow = new ChannelsWindow();
        searchWindow = new SearchWindow();
    }

    //delegations

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

    void clearVideosPanel() {
        mainWindow.clearVideosPanel();
    }

    void addVideoEntry(Video video, MouseAdapter mouseAdapter) {
        try {
            mainWindow.addVideoEntryToPanel(video, mouseAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateVideosPanel() {
        mainWindow.updateVideosPanel();
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


    void clearSearchResults() {
        searchWindow.clearSearchResults();
    }
    void addResultEntry(Channel channel, MouseAdapter mouseAdapter) {
        searchWindow.addResultEntry(channel, mouseAdapter);
    }
    void updateSearchResults() {
        searchWindow.updateSearchResults();
    }

    void clearChannelsPanel() {
        channelsWindow.clearChannelsPanel();
    }

    void addChannelEntry(Channel channel, MouseAdapter mouseAdapter) {
        channelsWindow.addChannelEntry(channel, mouseAdapter);
    }

    void updateChannelsPanel() {
        channelsWindow.updateChannelsPanel();
    }

}
