package com.boardgames;

import javafx.application.Application;
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
import javafx.scene.input.KeyCombination;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // Load title background
        Image bgImage = new Image(getClass().getResource("/assets/backgrounds/title_screen_background.jpeg").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        // Title text
        Font titleFont = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 80);
        Text titleText = new Text("ANCIENT BOARD GAMES");
        titleText.setFont(titleFont);
        titleText.setFill(Color.web("#F5F1E6"));
        titleText.setEffect(new DropShadow(10, Color.color(0, 0, 0, 0.5)));
        titleText.setTranslateY(-40);

        // Create buttons with images and hover/press effects
        Font buttonFont = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 40);
        Button bSenet = createImageButton("Senet", "/assets/backgrounds/senet_background.jpeg", buttonFont);
        Button bUr = createImageButton("Royal Game of Ur", "/assets/backgrounds/ur_background.jpg", buttonFont);
        Button bMorris = createImageButton("Men's Morris", "/assets/backgrounds/morris_background.jpg", buttonFont);
        Button bMancala = createImageButton("Mancala", "/assets/backgrounds/mancala_background.jpg", buttonFont);
        Button bGo = createImageButton("Go", "/assets/backgrounds/go_background.jpg", buttonFont);

        VBox buttonsVBox = new VBox(12, bSenet, bUr, bMorris, bMancala, bGo);
        buttonsVBox.setAlignment(Pos.CENTER);

        VBox mainBox = new VBox(10, titleText, buttonsVBox);
        mainBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(background, mainBox);

        Scene scene = new Scene(root, 1280, 720);

        // Make background scale
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        // Make buttons scale horizontally
        double widthFraction = 0.4;
        for (Button b : new Button[]{bSenet, bUr, bMorris, bMancala, bGo}) {
            b.prefWidthProperty().bind(scene.widthProperty().multiply(widthFraction));
            b.setPrefHeight(35);
        }

        stage.setTitle("Ancient Board Games");
        stage.setScene(scene);

        // Force fullscreen and disable default fullscreen exit key so the app stays fixated
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setResizable(false);

        stage.show();
    }

    private Button createImageButton(String text, String imagePath, Font font) {
        // Make text uppercase
        text = text.toUpperCase();

        Button btn = new Button(text);
        btn.setTextFill(Color.web("#F5F1E6"));
        btn.setFont(font);
        btn.setPrefHeight(64);

        // Load background image
        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        btn.setBackground(new Background(backgroundImage));

        // Drop shadow for depth
        DropShadow shadow = new DropShadow(5, Color.color(0, 0, 0, 0.4));
        btn.setEffect(shadow);

        // Hover effect: slightly brighten image
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        // Press effect: slightly darken
        btn.setOnMousePressed(e -> btn.setOpacity(0.7));
        btn.setOnMouseReleased(e -> btn.setOpacity(0.85));

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
