package com.boardgames.ui.screens;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;

public class DifficultyScreen extends StackPane {

    public DifficultyScreen(String gameBackgroundPath, Runnable onEasy, Runnable onMedium, Runnable onHard, Runnable onBack) {

        // ----- FULLSCREEN BACKGROUND (same as title screen) -----
        Image bgImage = new Image(getClass().getResource("/assets/backgrounds/title_screen_background.jpeg").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        widthProperty().addListener((obs, oldVal, newVal) -> background.setFitWidth(newVal.doubleValue()));
        heightProperty().addListener((obs, oldVal, newVal) -> background.setFitHeight(newVal.doubleValue()));

        // ----- SAME BUTTON FONT AS TITLE SCREEN -----
        Font buttonFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"),
                40
        );

        // ----- BUTTON BACKGROUND SAME AS GAME BUTTON -----
        Image img = new Image(getClass().getResource(gameBackgroundPath).toExternalForm());
        BackgroundImage buttonBg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        // ----- BUTTONS -----
        Button bEasy = new Button("EASY");
        Button bMedium = new Button("MEDIUM");
        Button bHard = new Button("HARD");
        Button bBack = new Button("BACK");

        for (Button b : new Button[]{bEasy, bMedium, bHard, bBack}) {
            b.setFont(buttonFont);
            b.setTextFill(Color.web("#F5F1E6"));
            b.setBackground(new Background(buttonBg));
            b.setPrefHeight(64);

            b.setOnMouseEntered(e -> b.setOpacity(0.85));
            b.setOnMouseExited(e -> b.setOpacity(1.0));
            b.setOnMousePressed(e -> b.setOpacity(0.7));
            b.setOnMouseReleased(e -> b.setOpacity(0.85));
        }

        // ----- WIDTH EXACTLY LIKE TITLE SCREEN -----
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                double widthFraction = 0.4;
                bEasy.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
                bMedium.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
                bHard.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
                bBack.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
            }
        });

        // ----- CALLBACKS -----
        bEasy.setOnAction(e -> onEasy.run());
        bMedium.setOnAction(e -> onMedium.run());
        bHard.setOnAction(e -> onHard.run());
        bBack.setOnAction(e -> onBack.run());

        // ----- JUST BUTTONS. NO TITLES. -----
        VBox box = new VBox(12, bEasy, bMedium, bHard, bBack);
        box.setAlignment(Pos.CENTER);

        getChildren().addAll(background, box);
    }
}
