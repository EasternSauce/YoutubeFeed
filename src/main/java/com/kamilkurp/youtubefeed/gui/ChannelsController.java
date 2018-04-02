package com.kamilkurp.youtubefeed.gui;

import com.kamilkurp.youtubefeed.model.ChannelData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class ChannelsController {

    @FXML
    private VBox channelList;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label statusBar;

    @FXML
    public void initialize() {
        channels = new LinkedList<>();

        loadChannelsFromFile(channelsFileName);


        refreshChannelList();

        channelList.setOnScroll(event -> {
            double deltaY = event.getDeltaY()*6;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            scrollPane.setVvalue(vValue + -deltaY/width);
        });

    }

    @FXML
    public void onBack() {
        Main.setScene("feed");
    }

    @FXML
    public void addNewChannels() {
        Main.setScene("channelsearch");
    }


    private List<ChannelData> channels;

    private String channelsFileName = "userchannels";

    public List<ChannelData> getChannels() {
        return channels;
    }

    public String getChannelsFileName() {
        return channelsFileName;
    }

    public void refreshChannelList(){
        channelList.getChildren().clear();

        List<ChannelData> channelsCopy = new LinkedList<>();

        for(ChannelData channel : channels) {
            channelsCopy.add(new ChannelData(channel));
        }

        for(ChannelData channel : channelsCopy) {
            HBox channelEntry = new HBox();
            VBox.setMargin(channelEntry, new Insets(10,10,10,10));
            Label label = new Label();
            label.setText(channel.getTitle());
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);


            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            channelEntry.setMaxWidth(200);

            Button button = new Button();

            button.setText("Remove");

            button.setFocusTraversable(false);

            button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

            button.setOnMouseClicked(e -> {
                removeChannelId(channel.getId());
                Platform.runLater(this::refreshChannelList);
            });




            channelEntry.getChildren().addAll(label, spacer, button);
            channelList.getChildren().add(channelEntry);
        }
    }



    private void loadChannelsFromFile(String fileName) {

        try {
            String channelId;

            File channelsFile = new File(fileName);

            boolean newFile = channelsFile.createNewFile();

            InputStream fis = new FileInputStream(fileName);

            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((channelId = br.readLine()) != null) {
                ChannelData channel = Main.getAuth().getChannel(channelId);
                channels.add(channel);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addChannel(ChannelData channel) {
        boolean exists = false;
        for(ChannelData c : channels) {
            if(c.getId().equals(channel.getId())) {
                exists = true;
                break;
            }
        }
        if(!exists) channels.add(channel);

    }

    private void removeChannelId(String channelId) {
        ChannelData toDelete = null;
        for(ChannelData channel : channels) {
            if(channel.getId().equals(channelId)) {
                toDelete = channel;
                break;
            }
        }
        channels.remove(toDelete);
    }





}
