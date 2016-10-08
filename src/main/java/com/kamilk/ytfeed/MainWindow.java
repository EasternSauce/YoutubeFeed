package com.kamilk.ytfeed;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URL;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Created by kamil on 2016-09-03.
 * Main window to display the video feed on.
 */

class MainWindow extends Window {
    private JButton updateButton;
    private JButton channelsButton;
    private JPanel videosPanel;
    private JProgressBar progressBar;
    private int progressVal;

    MainWindow() {
        super("YoutubeFeed");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(50, 30, 850, screenSize.height - 100);


        updateButton = new JButton("Update");
        channelsButton = new JButton("Channels");

        videosPanel = new JPanel();
        videosPanel.setLayout(new BoxLayout(videosPanel, BoxLayout.Y_AXIS));

        JScrollPane videosScrollPane = new JScrollPane(videosPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        videosScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        videosScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);

        addComponent(updateButton, 0, 0, 1, 1);
        addComponent(channelsButton, 1, 0, 1, 1);
        addComponent(videosScrollPane, 0, 1, 2, 1);
        addComponent(progressBar, 0, 2, 2, 1);

        addMainPanel();
    }

    void addUpdateButtonListener(ActionListener actionListener) {
        updateButton.addActionListener(actionListener);
    }

    void addChannelsButtonListener(ActionListener actionListener) {
        channelsButton.addActionListener(actionListener);
    }

    /**
     * videos panel informs that loading is ocurring
     */
    void showLoadingScreen() {
        videosPanel.removeAll();
        JLabel loading = new JLabel("Loading...");
        videosPanel.add(loading);
        videosPanel.revalidate();
        videosPanel.repaint();
    }

    void clearVideosPanel() {
        videosPanel.removeAll();
    }

    void addVideoEntryToPanel(Video video, MouseAdapter mouseAdapter) throws IOException{
        JPanel videoEntry = new JPanel();
        videoEntry.setLayout(new FlowLayout(FlowLayout.LEFT));
        videoEntry.setBackground(Color.GRAY);

        JPanel videoInfo = new JPanel();

        videoInfo.setLayout(new BoxLayout(videoInfo, BoxLayout.Y_AXIS));
        videoInfo.setBackground(Color.GRAY);

        JLabel thumbnail = new JLabel(new ImageIcon(ImageIO.read(new URL(video.getThumbnailUrl()))));


        videoEntry.add(thumbnail);


        JLabel title = new JLabel(video.getTitle());


        videoInfo.add(title);

        JLabel published = new JLabel("Published on " + video.getPrettyDate());


        videoInfo.add(published);

        JLabel channel = new JLabel("By " + video.getChannelTitle());


        videoInfo.add(channel);


        videoEntry.add(videoInfo);
        videosPanel.add(videoEntry);

        JLabel space = new JLabel(" ");
        videosPanel.add(space);

        makeClickable(videoEntry, mouseAdapter);
    }

    void updateVideosPanel() {
        progressBar.setValue(0);

        videosPanel.revalidate();
        videosPanel.repaint();
    }

    //progress bar manipulation methods

    void setProgressMax(int size) {
        progressBar.setMaximum(size);
    }

    void increaseProgress(int i) {
        progressVal += i;
        progressBar.setValue(progressVal);
    }

    void resetProgress() {
        progressBar.setValue(0);
        progressVal = 0;
    }


}
