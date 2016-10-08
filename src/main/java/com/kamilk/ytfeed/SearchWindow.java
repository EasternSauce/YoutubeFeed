package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Created by kamil on 2016-09-03.
 * Window where you can search for new channel to add to channel list.
 */

class SearchWindow extends Window {
    private JTextField searchBox;
    private JButton searchButton;
    private JPanel channelsPanel;

    SearchWindow() {
        super("Search for channel");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);


        setBounds(200, 180, 400, 480);

        searchBox = new JTextField();
        searchBox.setPreferredSize(new Dimension(200, 30));
        searchButton = new JButton("Search");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        addComponent(searchBox, 0, 0, 1, 1);
        addComponent(searchButton, 1, 0, 1, 1);
        addComponent(channelsScrollPane, 0, 1, 2, 1);

        addMainPanel();
    }

    void addSearchButtonListener(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    String getSearchedTerm() {
        return searchBox.getText();
    }

    void clearSearchResults() {
        channelsPanel.removeAll();
    }

    void addResultEntry(Channel channel, MouseAdapter mouseAdapter) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        JLabel title = new JLabel(channel.getTitle() + "   ");
        JLabel addLink = new JLabel("(add)");

        entry.add(title);
        entry.add(addLink);
        channelsPanel.add(entry);

        makeClickable(addLink, mouseAdapter);
    }

    void updateSearchResults() {
        channelsPanel.revalidate();
        channelsPanel.repaint();
    }
}
