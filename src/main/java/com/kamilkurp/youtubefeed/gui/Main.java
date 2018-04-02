package com.kamilkurp.youtubefeed.gui;

import com.kamilkurp.youtubefeed.model.ChannelData;
import com.kamilkurp.youtubefeed.ytapi.Auth;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main extends Application {
    private static Auth auth;

    private static Scene feedScene;
    private static Scene channelsScene;
    private static Scene channelSearchScene;

    private static Stage primaryStage;

    private static FXMLLoader feedLoader;
    private static FXMLLoader channelsLoader;
    private static FXMLLoader channelSearchLoader;


    public static void setScene(String scene) {
        if(scene.equals("feed")) {
            primaryStage.setScene(feedScene);

        }
        if(scene.equals("channels")) {
            primaryStage.setScene(channelsScene);
        }

        if(scene.equals("channelsearch")) {
            primaryStage.setScene(channelSearchScene);
        }
    }

    public static ChannelsController getChannelsController() {
        return channelsLoader.getController();
    }

    public static FeedController getFeedController() {
        return channelsLoader.getController();
    }

    public static ChannelSearchController getChannelSearchController() {
        return channelSearchLoader.getController();
    }

    public static Auth getAuth(){
        return auth;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;

        feedLoader = new FXMLLoader(getClass().getResource("/feed.fxml"));
        channelsLoader = new FXMLLoader(getClass().getResource("/channels.fxml"));
        channelSearchLoader = new FXMLLoader(getClass().getResource("/channelsearch.fxml"));

        Parent feedRoot = feedLoader.load();
        Parent channelsRoot = channelsLoader.load();
        Parent channelSearchRoot = channelSearchLoader.load();

        feedScene = new Scene(feedRoot);
        channelsScene = new Scene(channelsRoot);
        channelSearchScene = new Scene(channelSearchRoot);

        primaryStage.setTitle("Youtube Feed 2.0");
        primaryStage.setScene(feedScene);
        primaryStage.show();




    }

    @Override
    public void stop(){
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(getChannelsController().getChannelsFileName(), "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(writer != null) {
            for (ChannelData channel : getChannelsController().getChannels()) {
                writer.println(channel.getId());
            }

            writer.close();

        }

        Platform.exit();
    }


    public static void main(String[] args) throws IOException, Auth.CredentialsException {
        auth = new Auth();
        auth.authorize();
        launch(args);
    }
}
