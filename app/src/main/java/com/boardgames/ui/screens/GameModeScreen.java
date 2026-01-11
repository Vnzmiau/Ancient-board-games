package com.boardgames.ui.screens;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;

public class GameModeScreen extends StackPane {

    public GameModeScreen(String gameBackgroundPath, Runnable onSingleplayer, Runnable onLocal2P, Runnable onBack) {

        // ----- FULLSCREEN BACKGROUND (same as title screen) -----
        Image bgImage = new Image(getClass().getResource("/assets/backgrounds/title_screen_background.jpeg").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        // Bind scaling like Main.java
        widthProperty().addListener((obs, oldVal, newVal) -> {
            background.setFitWidth(newVal.doubleValue());
        });

        heightProperty().addListener((obs, oldVal, newVal) -> {
            background.setFitHeight(newVal.doubleValue());
        });

        // ----- BUTTON FONT (EXACT SAME AS TITLE SCREEN) -----
        Font buttonFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"),
                40
        );

        // ----- BUTTON BACKGROUND IMAGE (same as selected game button) -----
        Image img = new Image(getClass().getResource(gameBackgroundPath).toExternalForm());
        BackgroundImage buttonBg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        // ----- BUTTONS -----
        Button bSingle = new Button("SINGLEPLAYER");
        Button bLocal = new Button("LOCAL 2-PLAYER");
        Button bBack = new Button("BACK");

        for (Button b : new Button[]{bSingle, bLocal, bBack}) {
            b.setFont(buttonFont);
            b.setTextFill(Color.web("#F5F1E6"));
            b.setBackground(new Background(buttonBg));
            b.setPrefHeight(64);                        // SAME AS TITLE SCREEN
            b.setOpacity(1.0);

            // hover effects (same as Main)
            b.setOnMouseEntered(e -> b.setOpacity(0.85));
            b.setOnMouseExited(e -> b.setOpacity(1.0));
            b.setOnMousePressed(e -> b.setOpacity(0.7));
            b.setOnMouseReleased(e -> b.setOpacity(0.85));
        }

        // ----- BUTTON WIDTH (EXACT SAME BINDING AS TITLE SCREEN) -----
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                double widthFraction = 0.4; // SAME AS MAIN
                bSingle.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
                bLocal.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
                bBack.prefWidthProperty().bind(newScene.widthProperty().multiply(widthFraction));
            }
        });

        // ----- BUTTON ACTIONS -----
        bSingle.setOnAction(e -> onSingleplayer.run());
        bLocal.setOnAction(e -> onLocal2P.run());
        bBack.setOnAction(e -> onBack.run());

        // ----- LAYOUT (NO TITLE, JUST BUTTONS) -----
        VBox box = new VBox(12, bSingle, bLocal, bBack);  // spacing = SAME AS MAIN (12)
        box.setAlignment(Pos.CENTER);

        getChildren().addAll(background, box);
    }
}
