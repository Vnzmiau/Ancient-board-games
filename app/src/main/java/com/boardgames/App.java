package com.boardgames;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // Load background image from resources
        Image bgImage = new Image(
                App.class.getResourceAsStream(
                        "/assets/backgrounds/title_screen_background.jpeg"
                )
        );

        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background);

        Scene scene = new Scene(root, 1280, 720);

        // Make background always fill the window
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Ancient Board Games");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
