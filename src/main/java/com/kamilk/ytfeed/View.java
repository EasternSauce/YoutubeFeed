package com.kamilk.ytfeed;

import java.awt.event.*;
import java.io.IOException;

/**
 * View class of MVC pattern. Mostly just delegates from window instances.
 */

class View {
    /**
     * Main window instance.
     */
    private final MainWindow mainWindow = new MainWindow();
    /**
     * Channels window instance.
     */
    private final ChannelsWindow channelsWindow = new ChannelsWindow();
    /**
     * Search window instance.
     */
    private final SearchWindow searchWindow = new SearchWindow();

    //////DELEGATED:
    void displayMainWindow() { mainWindow.display(); }
    void addMainWindowUpdateButtonListener(final ActionListener actionListener) { mainWindow.addUpdateButtonListener(actionListener); }
    void addMainWindowChannelsButtonListener(final ActionListener actionListener) { mainWindow.addChannelsButtonListener(actionListener); }
    void addChannelsWindowAddButtonListener(final ActionListener actionListener) { channelsWindow.addAddButtonListener(actionListener); }
    void addSearchWindowSearchButtonListener(final ActionListener actionListener) { searchWindow.addSearchButtonListener(actionListener); }
    void addChannelsWindowListener(final WindowAdapter windowAdapter) { channelsWindow.addWindowListener(windowAdapter); }
    String getSearchWindowSearchedTerm() { return searchWindow.getSearchedTerm(); }
    void displayChannelsWindow() { channelsWindow.setVisible(true); }
    void showMainWindowLoadingScreen() { mainWindow.showLoadingScreen(); }
    void clearMainWindowVideosPanel() { mainWindow.clearVideosPanel(); }
    void addMainWindowVideoEntry(final Video video, final MouseAdapter mouseAdapter) throws IOException { mainWindow.addVideoEntryToPanel(video, mouseAdapter); }
    void updateMainWindowVideosPanel() { mainWindow.updateVideosPanel(); }
    void showSearchWindowChannelSearch() { searchWindow.setVisible(true); }
    void setMainWindowProgressMax(final int size) { mainWindow.setProgressMax(size); }
    void increaseMainWindowProgress(final int i) { mainWindow.increaseProgress(i); }
    void resetMainWindowProgress() { mainWindow.resetProgress(); }
    void clearSearchWindowSearchResults() { searchWindow.clearChannelsPanel(); }
    void addSearchWindowResultEntry(final Channel channel, final MouseAdapter mouseAdapter) { searchWindow.addResultEntry(channel, mouseAdapter); }
    void updateSearchWindowSearchResults() { searchWindow.updateSearchResults(); }
    void clearChannelsWindowChannelsPanel() { channelsWindow.clearChannelsPanel(); }
    void addChannelsWindowChannelEntry(final Channel channel, final MouseAdapter mouseAdapter) { channelsWindow.addChannelEntry(channel, mouseAdapter); }
    void updateChannelsWindowChannelsPanel() { channelsWindow.updateChannelsPanel(); }

}
