package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Displaying and managing list of channels.
 */
class ChannelsWindow extends Window {
    /**
     * Button to add new channel.
     */
    private final JButton addButton;
    /**
     * Panel which lists every channel the user added.
     */
    private final JPanel channelsPanel;

    /**
     * Constuctor which sets window options, initializes window components ands adds them to main panel.
     */
    ChannelsWindow() {
        super("Channels"); //window name label
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        setBounds(150, 130, 400, 480);

        addButton = new JButton("Add");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        final JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        addComponent(addButton, 0, 0, 1, 1);
        addComponent(channelsScrollPane, 0, 1, 1, 1);

        addToMainPanel();
    }

    /**
     * Clears the panel component which displays channels. Used before refreshing channels panel.
     */
    void clearChannelsPanel() {
        channelsPanel.removeAll();
    }

    /**
     * Adds entries to panel one by one during the refresh.
     * @param channel channel to add
     * @param mouseAdapter click listener
     */
    void addChannelEntry(final Channel channel, final MouseAdapter mouseAdapter) {
        final JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        final JLabel title = new JLabel(channel.getTitle() + "   ");
        final JLabel remove = new JLabel("(remove)");
        entry.add(title);
        entry.add(remove);
        channelsPanel.add(entry);
        makeClickable(remove, mouseAdapter);

    }

    /**
     * Redraws the component that displays channels. Used after refreshing channels panel.
     */
    void updateChannelsPanel() {
        channelsPanel.revalidate();
        channelsPanel.repaint();
    }

    /**
     * Adds listener to 'add channel' button.
     * @param actionListener listener to button push
     */
    void addAddButtonListener(final ActionListener actionListener) {
        addButton.addActionListener(actionListener);
    }
}
