package com.kamilkurp.youtubefeed.gui;

import com.kamilkurp.youtubefeed.model.ChannelData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.io.IOException;

public class ChannelSearchController {

    @FXML
    private VBox searchResults;

    @FXML
    private TextField searchInput;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label statusBar;

    @FXML
    public void initialize() {
        searchResults.setOnScroll(event -> {
            double deltaY = event.getDeltaY()*6;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            scrollPane.setVvalue(vValue + -deltaY/width);
        });
    }

    @FXML
    public void onBack() {
        Main.setScene("channels");

        Main.getChannelsController().refreshChannelList();
    }

    @FXML
    public void onSearch() {
        List<ChannelData> channelsFound = null;

        try {
            channelsFound = Main.getAuth().searchForChannels(searchInput.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchResults.getChildren().clear();

        if(channelsFound != null) {
            for (final ChannelData result : channelsFound) {
                HBox searchResult = new HBox();
                searchResult.setMaxWidth(200);

                VBox.setMargin(searchResult, new Insets(10, 10, 10, 10));
                Label label = new Label();
                label.setText(result.getTitle());
                label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button button = new Button();
                button.setText("Add");
                button.setOnMouseClicked(event -> {
                    Main.getChannelsController().addChannel(result);
                    statusBar.setText("Channel \"" + result.getTitle() + "\" added to channel list");

                });
                button.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

                searchResult.getChildren().addAll(label, spacer, button);

                searchResults.getChildren().add(searchResult);

            }
        }




    }
}
