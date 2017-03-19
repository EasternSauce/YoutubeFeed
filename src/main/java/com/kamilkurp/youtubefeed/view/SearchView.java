package com.kamilkurp.youtubefeed.view;

import com.kamilkurp.youtubefeed.model.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Window where you can search for new channel to add to channel list.
 */
public class SearchView extends WindowView {
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
    public SearchView() {
        super("Search for channel");
        setDefaultCloseOperation(HIDE_ON_CLOSE);


        setBounds(200, 180, 1000, 900);

        searchBox = new JTextField();
        searchBox.setPreferredSize(new Dimension(700, 60));
        searchButton = new JButton("Search");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        final JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 100, (int) getBounds().getHeight() - 200));

        addComponent(searchBox, 0, 0, 1, 1, 0.1f, 0.1f);
        addComponent(searchButton, 1, 0, 1, 1, 0.1f, 0.1f);
        addComponent(channelsScrollPane, 0, 1, 2, 1, 0.1f, 0.1f);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 100, (int) getBounds().getHeight() - 200));
            }
        });

        addMainPanel();
    }

    /**
     * Adds listener to search button.
     * @param actionListener button push listener
     */
    public void addSearchButtonListener(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    /**
     * Gives access to user's search keywords.
     * @return search box text
     */
    public String getSearchedTerm() {
        return searchBox.getText();
    }

    /**
     * Clear the channel completely.
     */
    public void clearChannelsPanel() {
        channelsPanel.removeAll();
    }

    /**
     * Add a result channel to display.
     * @param channel channel to display
     * @param mouseAdapter add link listener
     */
    public void addResultEntry(Channel channel, MouseAdapter mouseAdapter) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        JLabel title = new JLabel(channel.getTitle() + "   ");
        JLabel addLink = new JLabel("(add)");

        entry.add(title);
        entry.add(addLink);
        channelsPanel.add(entry);

        makeClickable(addLink, mouseAdapter);
    }

    /**
     * update and draw the changes
     */
    public void updateSearchResults() {
        changeFont(channelsPanel, new Font("Verdana", Font.BOLD, 24));
        channelsPanel.revalidate();
        channelsPanel.repaint();

    }
}
