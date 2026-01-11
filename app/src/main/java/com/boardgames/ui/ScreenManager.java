package com.boardgames.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

public class ScreenManager {

    private static Stage primaryStage;
    private static Scene mainScene;

    public static void init(Stage stage, Pane initialRoot) {
        primaryStage = stage;

        mainScene = new Scene(initialRoot);

        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void show(Pane root) {
        mainScene.setRoot(root);
    }
}
