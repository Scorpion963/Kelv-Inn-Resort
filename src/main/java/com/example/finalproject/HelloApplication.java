package com.example.finalproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // loads the FXML layout for the main UI
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml")); // loads the main fxml file
        Scene scene = new Scene(fxmlLoader.load());
        // applies CSS styling
        scene.getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());

        // configures window properties
        stage.setResizable(true);
        stage.setHeight(900);
        stage.setWidth(1200);
        stage.setMinHeight(600);
        stage.setMinWidth(950);
        stage.setTitle("Kelv-Inn Resort");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // entry point for JavaFX application
    }
}