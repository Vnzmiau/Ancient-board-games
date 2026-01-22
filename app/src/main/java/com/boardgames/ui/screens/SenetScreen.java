package com.boardgames.ui.screens;

import com.boardgames.games.senet.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class SenetScreen extends StackPane {

    // ==================== CONFIGURABLE CONSTANTS ====================
    // Board configuration
    private static final int ROWS = 3;
    private static final int COLS = 10;
    private static final double CELL_SIZE = 74;
    private static final double BOARD_WIDTH = COLS * CELL_SIZE;
    private static final double BOARD_HEIGHT = ROWS * CELL_SIZE;
    
    // Piece sizes (configurable)
    private static final double WHITE_PIECE_SIZE = 75;
    private static final double BLACK_PIECE_SIZE = 60;
    
    // Dice sticks size (configurable)
    private static final double DICE_WIDTH = 120;
    private static final double DICE_HEIGHT = 150;
    
    // Spacing (configurable)
    private static final double SPACING_DICE_TO_BOARD = 90;

    private final SenetGame game = new SenetGame();
    private final GridPane boardPane = new GridPane();
    private final Map<Integer, StackPane> cellMap = new HashMap<>();
    private final Map<SenetPiece, ImageView> pieceMap = new HashMap<>();

    private Text statusText;
    private Text playerText;
    private HBox diceDisplay;
    private ImageView[] diceSticks = new ImageView[4];

    // Dragging state
    private SenetPiece draggingPiece;
    private ImageView draggingVisual;
    private StackPane origPositionCell;
    private Point2D dragOffset = Point2D.ZERO;

    public SenetScreen(Stage stage) {
        // Background
        ImageView bg = new ImageView(new Image(
                getClass().getResource("/assets/backgrounds/senet_background.jpeg").toExternalForm()
        ));
        bg.setPreserveRatio(false);
        bg.fitWidthProperty().bind(widthProperty());
        bg.fitHeightProperty().bind(heightProperty());

        // Build components
        buildBoard();
        placeInitialPieces();
        setupBoardEventHandlers();
        createDiceUI();

        // Status texts
        statusText = new Text("Roll the dice to start!");
        statusText.setFill(Color.BLACK);
        statusText.setFont(new Font(16));

        playerText = new Text("White's Turn");
        playerText.setFill(Color.BLACK);
        playerText.setFont(new Font(20));

        // Board styling - center alignment for GridPane with NO GAPS
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setHgap(0);  // No horizontal gap
        boardPane.setVgap(0);  // No vertical gap
        boardPane.setStyle("-fx-background-color: transparent;");

        // Wrap board in a centered container
        StackPane boardContainer = new StackPane(boardPane);
        boardContainer.setAlignment(Pos.CENTER);

        // Layout - centered
        VBox topSection = new VBox(10, playerText, statusText);
        topSection.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(SPACING_DICE_TO_BOARD, topSection, boardContainer, diceDisplay);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        // Center everything properly on screen
        StackPane centerLayout = new StackPane();
        centerLayout.getChildren().add(mainLayout);
        StackPane.setAlignment(mainLayout, Pos.CENTER);

        getChildren().addAll(bg, centerLayout);
        
        // Ensure proper centering by binding layout to center
        centerLayout.prefWidthProperty().bind(widthProperty());
        centerLayout.prefHeightProperty().bind(heightProperty());
    }

    // ==================== BOARD SETUP ====================

    private void buildBoard() {
        // Load square images
        Image squareWhite = new Image(getClass().getResource("/assets/senet/white_square.png").toExternalForm());
        Image squareRed = new Image(getClass().getResource("/assets/senet/red_square.png").toExternalForm());

        // Load special numbered squares
        Image square15 = new Image(getClass().getResource("/assets/senet/15_square.png").toExternalForm());
        Image square26 = new Image(getClass().getResource("/assets/senet/26_square.png").toExternalForm());
        Image square27 = new Image(getClass().getResource("/assets/senet/27_square.png").toExternalForm());
        Image square28 = new Image(getClass().getResource("/assets/senet/28_square.png").toExternalForm());
        Image square29 = new Image(getClass().getResource("/assets/senet/29_square.png").toExternalForm());

        // Create 3x10 grid with proper board flow (NO VISIBLE BORDERS)
        int[][] boardLayout = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},        // Row 0: 1-10
                {20, 19, 18, 17, 16, 15, 14, 13, 12, 11}, // Row 1: 20-11 (reverse)
                {21, 22, 23, 24, 25, 26, 27, 28, 29, 30}   // Row 2: 21-30
        };

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int squareNum = boardLayout[row][col];

                // Select image based on square number
                Image squareImg;
                if (squareNum == 15) {
                    squareImg = square15;
                } else if (squareNum == 26) {
                    squareImg = square26;
                } else if (squareNum == 27) {
                    squareImg = square27;
                } else if (squareNum == 28) {
                    squareImg = square28;
                } else if (squareNum == 29) {
                    squareImg = square29;
                } else if (squareNum % 2 == 1) {
                    // Odd squares are RED
                    squareImg = squareRed;
                } else {
                    // Even squares are WHITE
                    squareImg = squareWhite;
                }

                // Create cell container
                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-border-color: transparent;"); // No visible borders

                // Create square image
                ImageView squareImageView = new ImageView(squareImg);
                squareImageView.setFitWidth(CELL_SIZE);
                squareImageView.setFitHeight(CELL_SIZE);
                squareImageView.setPreserveRatio(false);

                // Hover highlight overlay
                Rectangle highlight = new Rectangle(CELL_SIZE, CELL_SIZE);
                highlight.setFill(Color.color(1, 1, 0, 0.15)); // Very subtle yellow
                highlight.setVisible(false);

                cell.getChildren().addAll(squareImageView, highlight);
                boardPane.add(cell, col, row);

                cellMap.put(squareNum, cell);
            }
        }
    }

    private void placeInitialPieces() {
        Image whitePieceImg = new Image(getClass().getResource("/assets/senet/white_piece.png").toExternalForm());
        Image blackPieceImg = new Image(getClass().getResource("/assets/senet/dark_piece.png").toExternalForm());

        for (int i = 1; i <= 10; i++) {
            SenetPiece piece = game.getBoard().getPieceAt(i);
            if (piece == null) continue;

            ImageView pieceView = new ImageView(piece.getColor() == PlayerColor.WHITE ? whitePieceImg : blackPieceImg);
            double size = piece.getColor() == PlayerColor.WHITE ? WHITE_PIECE_SIZE : BLACK_PIECE_SIZE;
            pieceView.setFitWidth(size);
            pieceView.setFitHeight(size);
            pieceView.setPreserveRatio(true);

            DropShadow shadow = new DropShadow(8, Color.BLACK);
            pieceView.setEffect(shadow);

            // Position piece in cell (centered)
            StackPane cell = cellMap.get(i);
            StackPane.setAlignment(pieceView, Pos.CENTER);
            cell.getChildren().add(pieceView);

            // Make piece draggable
            pieceView.setOnMousePressed(e -> startDraggingPiece(piece, pieceView, e));
            pieceView.setOnMouseDragged(e -> dragPiece(pieceView, e));
            pieceView.setOnMouseReleased(e -> finishDraggingPiece(pieceView, e));
            pieceView.setCursor(javafx.scene.Cursor.HAND);

            pieceMap.put(piece, pieceView);
        }
    }

    private void setupBoardEventHandlers() {
        boardPane.addEventFilter(MouseEvent.MOUSE_MOVED, this::highlightSquare);
        boardPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::highlightSquare);
        boardPane.addEventFilter(MouseEvent.MOUSE_EXITED, e -> clearAllHighlights());
    }

    // ==================== DICE UI ====================

    private void createDiceUI() {
        diceDisplay = new HBox(15);
        diceDisplay.setAlignment(Pos.CENTER);
        diceDisplay.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 10;");
        diceDisplay.setOpacity(1.0);
        diceDisplay.setDisable(false);  // Start ENABLED for initial roll

        Image darkDice = new Image(getClass().getResource("/assets/senet/dark_side_dice_stick.png").toExternalForm());

        // Create 4 dice sticks - initially all dark (rounded sides up)
        for (int i = 0; i < 4; i++) {
            diceSticks[i] = new ImageView(darkDice);
            diceSticks[i].setFitWidth(DICE_WIDTH);
            diceSticks[i].setFitHeight(DICE_HEIGHT);
            diceSticks[i].setPreserveRatio(true);

            diceDisplay.getChildren().add(diceSticks[i]);
        }
        
        // Make entire dice area clickable
        diceDisplay.setOnMouseClicked(e -> rollDice());
    }

    private void rollDice() {
        int result = game.rollDice();
        updateDiceDisplay(result);
        refreshBoard();  // Refresh board to show dark piece movement if applicable
        updateStatus();
        System.out.println("[SENET] Rolled: " + result);
    }

    private void updateDiceDisplay(int rollResult) {
        Image whiteDice = new Image(getClass().getResource("/assets/senet/white_side_dice_stick.png").toExternalForm());
        Image darkDice = new Image(getClass().getResource("/assets/senet/dark_side_dice_stick.png").toExternalForm());

        // Update dice images: first 'rollResult' sticks show white (flat), rest show dark (rounded)
        for (int i = 0; i < 4; i++) {
            if (i < rollResult) {
                diceSticks[i].setImage(whiteDice);  // Flat side up = white
            } else {
                diceSticks[i].setImage(darkDice);   // Rounded side up = dark
            }
        }
    }

    // ==================== INTERACTION ====================

    private void startDraggingPiece(SenetPiece piece, ImageView visual, MouseEvent evt) {
        if (!game.isMoveHasPending()) {
            statusText.setText("Roll the dice first!");
            return;
        }

        if (piece.getColor() != game.getCurrentPlayer()) {
            statusText.setText("Not your piece!");
            return;
        }

        // Check if this piece can move forward
        if (!game.canMoveForward(piece)) {
            // If cannot move forward, check if any other pieces can move forward
            if (game.hasAnyValidMoveForward(game.getCurrentPlayer())) {
                statusText.setText("This piece is blocked! Move another piece forward.");
                return;
            }
            // Only allow backward if no pieces can move forward
            if (!game.canMoveBackward(piece)) {
                statusText.setText("This piece cannot move!");
                return;
            }
        }

        draggingPiece = piece;
        draggingVisual = visual;
        origPositionCell = cellMap.get(piece.getPosition());

        dragOffset = new Point2D(evt.getX(), evt.getY());
        visual.setOpacity(0.6);
        visual.toFront();

        // Highlight valid destination squares
        int roll = game.getLastRoll();
        int current = piece.getPosition();
        
        // Always try forward first
        if (game.canMoveForward(piece)) {
            highlightCell(current + roll, true);
        } else if (game.canMoveBackward(piece)) {
            // Only suggest backward if forward is impossible
            highlightCell(current - roll, true);
        }

        statusText.setText("Dragging piece from square " + piece.getPosition() + " - Roll: " + roll);
    }

    private void dragPiece(ImageView visual, MouseEvent evt) {
        if (draggingVisual != visual) return;

        Point2D sceneCoords = new Point2D(evt.getSceneX(), evt.getSceneY());
        Point2D boardCoords = boardPane.sceneToLocal(sceneCoords);

        // Keep piece within bounds
        double newX = boardCoords.getX() - dragOffset.getX();
        double newY = boardCoords.getY() - dragOffset.getY();

        if (newX >= 0 && newX <= BOARD_WIDTH && newY >= 0 && newY <= BOARD_HEIGHT) {
            visual.setLayoutX(newX);
            visual.setLayoutY(newY);
        }

        evt.consume();
    }

    private void finishDraggingPiece(ImageView visual, MouseEvent evt) {
        if (draggingVisual != visual) return;

        Point2D sceneCoords = new Point2D(evt.getSceneX(), evt.getSceneY());
        Integer targetSquare = findSquareAt(sceneCoords);

        if (targetSquare != null && isValidMove(draggingPiece, targetSquare)) {
            // Execute move
            boolean success = game.movePiece(draggingPiece);
            if (success) {
                // Clear all highlights before refreshing board
                clearAllHighlights();
                refreshBoard();
                updateStatus();
            } else {
                clearAllHighlights();
                snapPieceBack(visual);
                updateStatus();  // Update status will re-enable dice if needed
            }
        } else {
            clearAllHighlights();
            snapPieceBack(visual);
            updateStatus();  // Update status will re-enable dice if needed
        }

        draggingPiece = null;
        draggingVisual = null;
        dragOffset = Point2D.ZERO;
    }

    private void snapPieceBack(ImageView visual) {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        StackPane cell = origPositionCell;
        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
        Point2D boardLocal = boardPane.sceneToLocal(cellBounds.getCenterX(), cellBounds.getCenterY());

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200),
                new KeyValue(visual.layoutXProperty(), boardLocal.getX() - WHITE_PIECE_SIZE / 2),
                new KeyValue(visual.layoutYProperty(), boardLocal.getY() - WHITE_PIECE_SIZE / 2),
                new KeyValue(visual.opacityProperty(), 1.0)
        ));

        timeline.play();
    }

    private Integer findSquareAt(Point2D sceneCoords) {
        for (Map.Entry<Integer, StackPane> entry : cellMap.entrySet()) {
            StackPane cell = entry.getValue();
            Bounds bounds = cell.localToScene(cell.getBoundsInLocal());

            if (bounds.contains(sceneCoords)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean isValidMove(SenetPiece piece, int targetSquare) {
        int roll = game.getLastRoll();
        int current = piece.getPosition();

        return (targetSquare == current + roll && game.canMoveForward(piece)) ||
                (targetSquare == current - roll && game.canMoveBackward(piece));
    }

    // ==================== HIGHLIGHTING ====================

    private void highlightSquare(MouseEvent evt) {
        if (draggingPiece != null) return;

        Point2D sceneCoords = new Point2D(evt.getSceneX(), evt.getSceneY());
        Integer square = findSquareAt(sceneCoords);

        clearAllHighlights();

        if (square != null) {
            StackPane cell = cellMap.get(square);
            Rectangle highlight = (Rectangle) cell.getChildren().get(1);
            highlight.setVisible(true);
        }
    }

    private void clearAllHighlights() {
        for (StackPane cell : cellMap.values()) {
            if (cell.getChildren().size() > 1) {
                Rectangle highlight = (Rectangle) cell.getChildren().get(1);
                // Reset to default yellow hover color
                highlight.setFill(Color.color(1, 1, 0, 0.15));
                highlight.setVisible(false);
            }
        }
    }

    private void highlightCell(int squareNum, boolean isValid) {
        StackPane cell = cellMap.get(squareNum);
        if (cell == null || cell.getChildren().size() < 2) return;
        
        Rectangle highlight = (Rectangle) cell.getChildren().get(1);
        if (isValid) {
            // Valid destination: bright green highlight
            highlight.setFill(Color.color(0, 1, 0, 0.3));
        } else {
            // Invalid: subtle yellow
            highlight.setFill(Color.color(1, 1, 0, 0.15));
        }
        highlight.setVisible(true);
    }

    // ==================== REFRESH ====================

    private void refreshBoard() {
        // Clear all highlights first
        clearAllHighlights();
        
        // Remove all piece views
        for (StackPane cell : cellMap.values()) {
            cell.getChildren().removeIf(n -> n instanceof ImageView && n != cell.getChildren().get(0));
        }

        // Reposition all pieces
        for (Map.Entry<SenetPiece, ImageView> entry : pieceMap.entrySet()) {
            SenetPiece p = entry.getKey();
            ImageView view = entry.getValue();

            if (!p.isOffBoard()) {
                StackPane cell = cellMap.get(p.getPosition());
                view.setLayoutX(0);
                view.setLayoutY(0);
                view.setOpacity(1.0);
                StackPane.setAlignment(view, Pos.CENTER);
                if (!cell.getChildren().contains(view)) {
                    cell.getChildren().add(view);
                }
            } else {
                view.setVisible(false);
            }
        }
    }

    private void updateDiceInteractivity(boolean enabled) {
        diceDisplay.setDisable(!enabled);
        diceDisplay.setStyle("-fx-background-color: transparent; -fx-cursor: " + (enabled ? "hand" : "not-allowed") + ";");
        
        // Update individual dice stick opacity
        double opacity = enabled ? 1.0 : 0.3;
        for (ImageView dice : diceSticks) {
            dice.setOpacity(opacity);
        }
    }

    private void updateStatus() {
        // Show Player 1/Player 2 before game starts, White/Black after dark piece owner is determined
        if (game.isNeedsInitialRoll() || !game.isGameStarted()) {
            String playerName = game.getCurrentPlayer() == PlayerColor.WHITE ? "Player 1" : "Player 2";
            playerText.setText(playerName + "'s Turn");
        } else {
            String playerColor = game.getCurrentPlayer() == PlayerColor.WHITE ? "White" : "Black";
            playerText.setText(playerColor + "'s Turn");
        }

        // Only enable dice when ready to roll (moveHasPending == false)
        if (!game.isMoveHasPending()) {
            updateDiceInteractivity(true);
        } else {
            // Block dice during move phase
            updateDiceInteractivity(false);
        }

        if (game.isGameOver()) {
            String winnerColor = game.getWinner() == PlayerColor.WHITE ? "White" : "Black";
            statusText.setText("🎉 " + winnerColor + " WINS! 🎉");
            updateDiceInteractivity(false);
            // Make dice even more transparent when game is over
            for (ImageView dice : diceSticks) {
                dice.setOpacity(0.2);
            }
        } else if (game.isNeedsInitialRoll()) {
            statusText.setText("Click the DICE to find dark piece owner!");
        } else if (game.isMoveHasPending()) {
            int roll = game.getLastRoll();
            String squareWord = roll > 1 ? "squares" : "square";
            statusText.setText("Move a piece " + roll + " " + squareWord + " - DRAG piece to highlighted square!");
        } else {
            String rollMsg = game.shouldRollAgain() ? " → ROLL AGAIN!" : " → Next Player";
            statusText.setText("Rolled " + game.getLastRoll() + rollMsg);
        }
    }
}
