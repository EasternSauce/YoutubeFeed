package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Created by kamil on 2016-09-03.
 * Displaying and managing list of channels.
 */

class ChannelsWindow extends Window {
    private JButton addButton;
    private JPanel channelsPanel;

    ChannelsWindow() {
        super("Channels");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        setBounds(150, 130, 400, 480);

        addButton = new JButton("Add");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        addComponent(addButton, 0, 0, 1, 1);
        addComponent(channelsScrollPane, 0, 1, 1, 1);

        addMainPanel();
    }

    void startUpdatingChannels() {
        channelsPanel.removeAll();
    }

    void updateChannel(Channel channel, MouseAdapter mouseAdapter) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        JLabel title = new JLabel(channel.getTitle() + "   ");
        JLabel remove = new JLabel("(remove)");
        entry.add(title);
        entry.add(remove);
        channelsPanel.add(entry);
        makeClickable(remove, mouseAdapter);

    }

    void finishUpdatingChannels() {
        channelsPanel.revalidate();
        channelsPanel.repaint();
    }

    void addAddButtonListener(ActionListener actionListener) {
        addButton.addActionListener(actionListener);
    }
}
