package com.boardgames.ui.screens;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class TitleScreen extends StackPane {

    public TitleScreen(Stage stage) {

        // ----- BACKGROUND -----
        Image bgImage = new Image(getClass().getResource("/assets/backgrounds/title_screen_background.jpeg").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        widthProperty().addListener((obs, o, n) -> background.setFitWidth(n.doubleValue()));
        heightProperty().addListener((obs, o, n) -> background.setFitHeight(n.doubleValue()));

        // ----- TITLE -----
        Font titleFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 80);

        Text titleText = new Text("ANCIENT BOARD GAMES");
        titleText.setFont(titleFont);
        titleText.setFill(Color.web("#F5F1E6"));
        titleText.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.5)));
        titleText.setTranslateY(-40);

        // ----- BUTTON FONT -----
        Font buttonFont = Font.loadFont(
                getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 40);

        // ----- GAME BUTTONS -----
        Button bSenet = createGameButton("Senet", "/assets/backgrounds/senet_background.jpeg", buttonFont, stage);
        Button bUr = createGameButton("Royal Game of Ur", "/assets/backgrounds/ur_background.jpg", buttonFont, stage);
        Button bMorris = createGameButton("Men's Morris", "/assets/backgrounds/morris_background.jpg", buttonFont, stage);
        Button bMancala = createGameButton("Mancala", "/assets/backgrounds/mancala_background.jpg", buttonFont, stage);
        Button bGo = createGameButton("Go", "/assets/backgrounds/go_background.jpg", buttonFont, stage);

        VBox buttonsVBox = new VBox(12, bSenet, bUr, bMorris, bMancala, bGo);
        buttonsVBox.setAlignment(Pos.CENTER);

        VBox mainBox = new VBox(10, titleText, buttonsVBox);
        mainBox.setAlignment(Pos.CENTER);

        getChildren().addAll(background, mainBox);
    }

    private Button createGameButton(String text, String imgPath, Font font, Stage stage) {

        text = text.toUpperCase();

        Button btn = new Button(text);
        btn.setFont(font);
        btn.setTextFill(Color.web("#F5F1E6"));
        btn.setPrefHeight(64);

        Image img = new Image(getClass().getResource(imgPath).toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        btn.setBackground(new Background(backgroundImage));

        // width binding — same as title screen
        btn.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                btn.prefWidthProperty().bind(newScene.widthProperty().multiply(0.4));
            }
        });

        // hover/press effects
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        btn.setOnMousePressed(e -> btn.setOpacity(0.7));
        btn.setOnMouseReleased(e -> btn.setOpacity(0.85));

        // CLICK → OPEN MODE SELECT SCREEN (this is the FIX)
        btn.setOnAction(e -> {
            // final Runnables (effectively final for lambda capture)
            final Runnable onEasy = imgPath.contains("senet_background") ? () -> stage.getScene().setRoot(new SenetScreen(stage)) : () -> {};
            final Runnable onMedium = imgPath.contains("senet_background") ? () -> stage.getScene().setRoot(new SenetScreen(stage)) : () -> {};
            final Runnable onHard = imgPath.contains("senet_background") ? () -> stage.getScene().setRoot(new SenetScreen(stage)) : () -> {};
            final Runnable onLocal2P = imgPath.contains("senet_background") ? () -> stage.getScene().setRoot(new SenetScreen(stage)) : () -> {};

            // Use a reference so the back action from Difficulty can return to the same GameModeScreen instance
            final GameModeScreen[] modeRef = new GameModeScreen[1];

            final Runnable backToMode = () -> stage.getScene().setRoot(modeRef[0]);
            final Runnable openDifficulty = () -> stage.getScene().setRoot(new DifficultyScreen(imgPath, onEasy, onMedium, onHard, backToMode));

            modeRef[0] = new GameModeScreen(imgPath, openDifficulty, onLocal2P, () -> stage.getScene().setRoot(new TitleScreen(stage)));

            stage.getScene().setRoot(modeRef[0]);
        });

        return btn;
    }
}
