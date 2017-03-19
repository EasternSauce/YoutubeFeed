package com.kamilk.ytfeed;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URL;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Main window to display the video feed on.
 */
class MainView extends WindowView {
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
    MainView() {
        super("YoutubeFeed");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(50, 30, 2000, 1200);

        updateButton = new JButton("Update");
        channelsButton = new JButton("Channels");

        videosPanel = new JPanel();
        videosPanel.setLayout(new BoxLayout(videosPanel, BoxLayout.Y_AXIS));

        final JScrollPane videosScrollPane = new JScrollPane(videosPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        videosScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        videosScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 100, (int) getBounds().getHeight() - 200));

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);

        GridBagConstraints c = new GridBagConstraints();


        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 1;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        mainPanel.add(updateButton, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        mainPanel.add(channelsButton, c);

        c.ipadx = 0;
        c.ipady = 0;
        c.insets = new Insets(0, 0, 0, 0);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        mainPanel.add(videosScrollPane, c);

        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        mainPanel.add(progressBar, c);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                videosScrollPane.setPreferredSize(new Dimension((int) getBounds().getWidth() - 100, (int) getBounds().getHeight() - 200));
            }
        });

        addMainPanel();
    }

    /**
     * Adds a listener for update button.
     * @param actionListener press listener
     */
    void addUpdateButtonListener(ActionListener actionListener) {
        updateButton.addActionListener(actionListener);
    }

    /**
     * Adds a listener for channels button.
     * @param actionListener press listener
     */
    void addChannelsButtonListener(ActionListener actionListener) {
        channelsButton.addActionListener(actionListener);
    }

    /**
     * Shows on videos panel that loading is ocurring.
     */
    void showLoadingScreen() {
        videosPanel.removeAll();
        JLabel loading = new JLabel("Loading...");
        videosPanel.add(loading);


        changeFont(videosPanel, new Font("Verdana", Font.BOLD, 24));
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
    void addVideoEntryToPanel(VideoData video, MouseAdapter mouseAdapter) throws IOException{
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

        JLabel published = new JLabel("Published on " + video.getPublishedAt());


        videoInfo.add(published);

        JLabel channel = new JLabel("By " + video.getChannelTitle());

        videoInfo.add(channel);

        JLabel duration = new JLabel("Duration: " + video.getDuration());

        videoInfo.add(duration);

        //final JLabel viewCount = new JLabel("Views: " + new String(video.getViewCount().toByteArray()));

        //videoInfo.add(viewCount);


        videoEntry.add(videoInfo);
        videosPanel.add(videoEntry);

        JLabel space = new JLabel(" ");
        videosPanel.add(space);


        makeClickable(videoEntry, mouseAdapter);
    }

    /**
     * Update and redraw the panel.
     */
    void updateVideosPanel() {
        progressBar.setValue(0);

        changeFont(videosPanel, new Font("Verdana", Font.BOLD, 24));

        videosPanel.revalidate();
        videosPanel.repaint();
    }

    //progress bar manipulation methods

    /**
     * Sets maximum progress bar value.
     * @param size maximum for progress bar
     */
    void setProgressMax(int size) {
        progressBar.setMaximum(size);
    }


    /**
     * Increases progres by an ammount.
     * @param i the increase
     */
    void increaseProgress(int i) {
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
