package com.boardgames.ui;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UIUtil {

    public static Button createImageButton(String text, String imagePath, Font font) {

        text = text.toUpperCase();

        Button btn = new Button(text);
        btn.setTextFill(Color.web("#F5F1E6"));
        btn.setFont(font);

        Image img = new Image(UIUtil.class.getResource(imagePath).toExternalForm());

        BackgroundImage backgroundImage = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        btn.setBackground(new Background(backgroundImage));

        btn.setEffect(new DropShadow(5, Color.color(0, 0, 0, 0.4)));

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        // Press effect
        btn.setOnMousePressed(e -> btn.setOpacity(0.7));
        btn.setOnMouseReleased(e -> btn.setOpacity(0.85));

        return btn;
    }
}
