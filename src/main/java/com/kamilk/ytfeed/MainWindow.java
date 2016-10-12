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
 * Main window to display the video feed on.
 */
class MainWindow extends Window {
    /**
     * Button to update video list.
     */
    private final JButton updateButton;
    /**
     * Button to see channels window.
     */
    private final JButton channelsButton;
    /**
     * Panel to display videos on.
     */
    private final JPanel videosPanel;
    /**
     * Progress bar for video list loading.
     */
    private final JProgressBar progressBar;
    /**
     * Value of progress bar.
     */
    private int progressVal;

    /**
     * Constructor of main window, sets window options, creates components, adds the window to main panel.
     */
    MainWindow() {
        super("YoutubeFeed");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(50, 30, 850, screenSize.height - 100);

        updateButton = new JButton("Update");
        channelsButton = new JButton("Channels");

        videosPanel = new JPanel();
        videosPanel.setLayout(new BoxLayout(videosPanel, BoxLayout.Y_AXIS));

        final JScrollPane videosScrollPane = new JScrollPane(videosPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        videosScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        videosScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 20, (int) getBounds().getHeight() - 75));

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);

        addComponent(updateButton, 0, 0, 1, 1);
        addComponent(channelsButton, 1, 0, 1, 1);
        addComponent(videosScrollPane, 0, 1, 2, 1);
        addComponent(progressBar, 0, 2, 2, 1);

        addToMainPanel();
    }

    /**
     * Adds a listener for update button.
     * @param actionListener press listener
     */
    void addUpdateButtonListener(final ActionListener actionListener) {
        updateButton.addActionListener(actionListener);
    }

    /**
     * Adds a listener for channels button.
     * @param actionListener press listener
     */
    void addChannelsButtonListener(final ActionListener actionListener) {
        channelsButton.addActionListener(actionListener);
    }

    /**
     * Shows on videos panel that loading is ocurring.
     */
    void showLoadingScreen() {
        videosPanel.removeAll();
        final JLabel loading = new JLabel("Loading...");
        videosPanel.add(loading);
        videosPanel.revalidate();
        videosPanel.repaint();
    }

    /**
     * Empties the videos panel completely.
     */
    void clearVideosPanel() {
        videosPanel.removeAll();
    }

    /**
     * Adds video and video information to the videos panel.
     * @param video video to add
     * @param mouseAdapter listener to video clicks
     */
    void addVideoEntryToPanel(final Video video, final MouseAdapter mouseAdapter) throws IOException{
        final JPanel videoEntry = new JPanel();
        videoEntry.setLayout(new FlowLayout(FlowLayout.LEFT));
        videoEntry.setBackground(Color.GRAY);

        final JPanel videoInfo = new JPanel();

        videoInfo.setLayout(new BoxLayout(videoInfo, BoxLayout.Y_AXIS));
        videoInfo.setBackground(Color.GRAY);

        final JLabel thumbnail = new JLabel(new ImageIcon(ImageIO.read(new URL(video.getThumbnailUrl()))));


        videoEntry.add(thumbnail);


        final JLabel title = new JLabel(video.getTitle());


        videoInfo.add(title);

        final JLabel published = new JLabel("Published on " + video.getPrettyDate());


        videoInfo.add(published);

        final JLabel channel = new JLabel("By " + video.getChannelTitle());


        videoInfo.add(channel);


        videoEntry.add(videoInfo);
        videosPanel.add(videoEntry);

        final JLabel space = new JLabel(" ");
        videosPanel.add(space);

        makeClickable(videoEntry, mouseAdapter);
    }

    /**
     * Update and redraw the panel.
     */
    void updateVideosPanel() {
        progressBar.setValue(0);

        videosPanel.revalidate();
        videosPanel.repaint();
    }

    //progress bar manipulation methods

    /**
     * Sets maximum progress bar value.
     * @param size maximum for progress bar
     */
    void setProgressMax(final int size) {
        progressBar.setMaximum(size);
    }


    /**
     * Increases progres by an ammount.
     * @param i the increase
     */
    void increaseProgress(final int i) {
        progressVal += i;
        progressBar.setValue(progressVal);
    }

    /**
     * Resets the progress bar to 0.
     */
    void resetProgress() {
        progressBar.setValue(0);
        progressVal = 0;
    }


}
