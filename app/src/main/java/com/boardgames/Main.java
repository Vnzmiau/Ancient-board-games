package com.boardgames;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

import com.boardgames.ui.screens.TitleScreen;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        TitleScreen title = new TitleScreen(stage);

        Scene scene = new Scene(title, 1280, 720);

        stage.setScene(scene);
        stage.setTitle("Ancient Board Games");

        // FULLSCREEN SAME AS TITLE SCREEN
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
