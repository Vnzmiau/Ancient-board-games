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

        Button bExit = new Button("EXIT");
        bExit.setFont(buttonFont);
        bExit.setTextFill(Color.web("#F5F1E6"));
        bExit.setPrefHeight(64);
        
      
        bExit.setStyle("-fx-background-color: rgba(60, 0, 0, 0.8); -fx-border-color: #F5F1E6; -fx-cursor: hand;");

        // Scaling logic for the Exit button
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                bExit.prefWidthProperty().bind(newScene.widthProperty().multiply(0.4));
            }
        });

        // Hover effects
        bExit.setOnMouseEntered(e -> bExit.setOpacity(0.8));
        bExit.setOnMouseExited(e -> bExit.setOpacity(1.0));

        // Action: Close the App
        bExit.setOnAction(e -> stage.close());

        // --- UPDATE THE VBOX ---
        // Add bExit to the list of children in the VBox
        VBox buttonsVBox = new VBox(12, bSenet, bUr, bMorris, bMancala, bGo, bExit);
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

        btn.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                btn.prefWidthProperty().bind(newScene.widthProperty().multiply(0.4));
            }
        });

        // Hover effects
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        // Inside TitleScreen.java or your controller
        btn.setOnAction(e -> {
            final GameModeScreen[] modeRef = new GameModeScreen[1];
            
            // 1. Define the 'Back' logic for the Difficulty Screen
            Runnable backToMode = () -> stage.getScene().setRoot(modeRef[0]);

            // 2. Define Singleplayer to OPEN DifficultyScreen, NOT the board
            Runnable onSingleplayer = () -> {
                // Create the game instance first (required by your DifficultyScreen constructor)
                SenetScreen senet = new SenetScreen(stage, backToMode);
                
                // Show the Difficulty Screen instead of the board
                DifficultyScreen diffScreen = new DifficultyScreen(imgPath, senet, backToMode);
                stage.getScene().setRoot(diffScreen);
            };

            // 3. Define Local 2P to go straight to the board
            Runnable onLocal2P = () -> {
                stage.getScene().setRoot(new SenetScreen(stage, backToMode));
            };

            // 4. Create the GameModeScreen
            modeRef[0] = new GameModeScreen(
                imgPath, 
                onSingleplayer, 
                onLocal2P, 
                () -> stage.getScene().setRoot(new TitleScreen(stage))
            );

            stage.getScene().setRoot(modeRef[0]);
        });

        return btn;
    }
}
