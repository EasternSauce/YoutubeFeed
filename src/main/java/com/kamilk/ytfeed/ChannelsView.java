package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Displaying and managing list of channels.
 */
class ChannelsView extends WindowView {
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
    ChannelsView() {
        super("Channels");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        setBounds(150, 130, 800, 960);

        addButton = new JButton("Add");

        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));

        final JScrollPane channelsScrollPane = new JScrollPane(channelsPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        channelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        channelsScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 100, (int) getBounds().getHeight() - 200));

        addComponent(addButton, 0, 0, 1, 1, 0.1f, 0.1f);
        addComponent(channelsScrollPane, 0, 1, 1, 1, 0.1f, 0.1f);

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
    void addChannelEntry(Channel channel, MouseAdapter mouseAdapter) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));
        JLabel title = new JLabel(channel.getTitle() + "   ");
        JLabel remove = new JLabel("(remove)");
        entry.add(title);
        entry.add(remove);
        channelsPanel.add(entry);
        makeClickable(remove, mouseAdapter);

    }

    /**
     * Redraws the component that displays channels. Used after refreshing channels panel.
     */
    void updateChannelsPanel() {
        changeFont(channelsPanel, new Font("Verdana", Font.BOLD, 24));
        channelsPanel.revalidate();
        channelsPanel.repaint();
    }

    /**
     * Adds listener to 'add channel' button.
     * @param actionListener listener to button push
     */
    void addAddButtonListener(ActionListener actionListener) {
        addButton.addActionListener(actionListener);
    }
}
