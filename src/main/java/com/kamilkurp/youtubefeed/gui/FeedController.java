package com.kamilkurp.youtubefeed.gui;

import com.kamilkurp.youtubefeed.model.ChannelData;
import com.kamilkurp.youtubefeed.model.VideoData;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class FeedController {

    @FXML
    private VBox videoList;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label statusBar;

    @FXML
    private HBox scrollPaneBox;

    private List<VideoData> videosSince = new LinkedList<>();


    @FXML
    public void initialize() {
        videoList.setOnScroll(event -> {
            double deltaY = event.getDeltaY()*6;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            scrollPane.setVvalue(vValue + -deltaY/width);
        });

        scrollPaneBox.setStyle("-fx-focus-color: transparent;");

        Platform.runLater(this::onRefresh);


    }

    @FXML
    public void onRefresh() {
        statusBar.setText("Refreshing...");

        videosSince.clear();

        List<ChannelData> channels = Main.getChannelsController().getChannels();



        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                for(ChannelData channel : channels) {
                    videosSince.addAll(fetchVideos(channel));
                }

                videosSince.sort((o1, o2) -> (int) (o1.getPublishedDate().getValue() - o2.getPublishedDate().getValue()));

                Collections.reverse(videosSince);


                Platform.runLater(() -> {
                    updateVideoList(videosSince);

                    statusBar.setText("Done");
                });

                return null;
            }
        };


        new Thread(task).start();


    }

    @FXML
    public void onChannels() {
        Main.setScene("channels");
    }


    private List<VideoData> fetchVideos(ChannelData channel) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));

        List<VideoData> videosSince = null;

        try {
            videosSince = Main.getAuth().getVideosSince(channel.getId(), sevenDaysAgo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videosSince;
    }

    private void updateVideoList(List<VideoData> videosSince) {
        videoList.getChildren().clear();

        for (final VideoData video : videosSince) {
            final HBox videoEntry = new HBox();

            Label label = new Label();
            label.setText(video.getTitle() +
                    "\nPublished on: " + video.getPublishedAt() +
                    "\nBy: " + video.getChannelTitle() +
                    "\nDuration: " + video.getDuration());

            ImageView imageView = new ImageView();

            videoEntry.getChildren().addAll(imageView, label);

            videoEntry.setSpacing(15);

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    Image image = new Image(video.getThumbnailUrl());

                    Platform.runLater(() -> imageView.setImage(image));

                    return null;
                }
            };

            new Thread(task).start();



            videoEntry.setOnMouseClicked(event -> {
                try {
                    Desktop.getDesktop().browse(new URI(video.getUrl()));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            });

            videoList.getChildren().add(videoEntry);

            videoList.setSpacing(15);


        }
    }


}
