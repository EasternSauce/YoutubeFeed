package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Window where you can search for new channel to add to channel list.
 */

class SearchWindow extends Window {
    /**
     * Search box to input search keywords.
     */
    private final JTextField searchBox;
    /**
     * Button to begin a search.
     */
    private final JButton searchButton;
    /**
     * Panel for displaying channels.
     */
    private final JPanel channelsPanel;

    /**
     * Constructor of search window, sets window options, creates components, adds the window to main panel.
     */
    SearchWindow() {
        super("Search for channel");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);


        setBounds(200, 180, 400, 480);

        searchBox = new JTextField();
        searchBox.setPreferredSize(new Dimension(200, 30));
        searchButton = new JButton("Search");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        final JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        addComponent(searchBox, 0, 0, 1, 1);
        addComponent(searchButton, 1, 0, 1, 1);
        addComponent(channelsScrollPane, 0, 1, 2, 1);

        addToMainPanel();
    }

    /**
     * Adds listener to search button.
     * @param actionListener button push listener
     */
    void addSearchButtonListener(final ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    /**
     * Gives access to user's search keywords.
     * @return search box text
     */
    String getSearchedTerm() {
        return searchBox.getText();
    }

    /**
     * Clear the channel completely.
     */
    void clearChannelsPanel() {
        channelsPanel.removeAll();
    }

    /**
     * Add a result channel to display.
     * @param channel channel to display
     * @param mouseAdapter add link listener
     */
    void addResultEntry(final Channel channel, final MouseAdapter mouseAdapter) {
        final JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        final JLabel title = new JLabel(channel.getTitle() + "   ");
        final JLabel addLink = new JLabel("(add)");

        entry.add(title);
        entry.add(addLink);
        channelsPanel.add(entry);

        makeClickable(addLink, mouseAdapter);
    }

    /**
     * update and draw the changes
     */
    void updateSearchResults() {
        channelsPanel.revalidate();
        channelsPanel.repaint();
    }
}
