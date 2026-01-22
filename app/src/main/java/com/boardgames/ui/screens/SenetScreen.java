package com.boardgames.ui.screens;


import com.boardgames.games.senet.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

    private StackPane exitCell; // virtual square 31 (bearing off)
    private final Runnable onBack;

    private Text statusText;
    private Text playerText;
    private HBox diceDisplay;
    private ImageView[] diceSticks = new ImageView[4];

    // Dragging state
    private SenetPiece draggingPiece;
    private ImageView dragProxy; // The "ghost" image that follows the cursor
    private ImageView draggingVisual;
    private StackPane origPositionCell;
    private double mouseAnchorX;
    private double mouseAnchorY;

    // AI
    private SenetAI ai;
    private boolean isVsAI = false;  // Enable for AI mode

    public SenetScreen(Stage stage, Runnable onBack) { 
        this.onBack = onBack;
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

        playerText = new Text("Player 1's Turn");
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

        createExitButton(stage);

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
                cell.setPickOnBounds(true);
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-border-color: transparent;"); // No visible borders

                // Create square image
                ImageView squareImageView = new ImageView(squareImg);
                squareImageView.setFitWidth(CELL_SIZE);
                squareImageView.setFitHeight(CELL_SIZE);
                squareImageView.setPreserveRatio(false);

                // Hover highlight overlay
                Rectangle highlight = new Rectangle(CELL_SIZE, CELL_SIZE);
                highlight.setId("highlight"); // <--- ADD THIS LINE
                highlight.setFill(Color.color(1, 1, 0, 0.15));
                highlight.setVisible(false);
                highlight.setMouseTransparent(true); // Ensure it doesn't block clicks

                cell.getChildren().addAll(squareImageView, highlight);
                boardPane.add(cell, col, row);

                cellMap.put(squareNum, cell);
            }
        }

            // ============================
        // VIRTUAL EXIT CELL (SQUARE 31)
        // ============================

        exitCell = new StackPane();
        exitCell.setPrefSize(CELL_SIZE, CELL_SIZE);
        exitCell.setAlignment(Pos.CENTER);
        exitCell.setPickOnBounds(true);

        Rectangle exitHighlight = new Rectangle(CELL_SIZE, CELL_SIZE);
        exitHighlight.setId("highlight");
        exitHighlight.setFill(Color.color(0, 1, 0, 0.4));
        exitHighlight.setVisible(false);
        exitHighlight.setMouseTransparent(true);

        exitCell.getChildren().add(exitHighlight);

        // Place exit cell visually to the RIGHT of square 30
        StackPane cell30 = cellMap.get(30);

        HBox exitWrapper = new HBox(exitCell);
        exitWrapper.setAlignment(Pos.CENTER_LEFT);
        exitWrapper.setTranslateX(CELL_SIZE * 0.5);

        boardPane.add(exitWrapper, COLS, 2); // row 2 = last row

        

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
        boardPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            clearAllHighlights();
            
            Integer hoverSquare = findSquareAt(e.getSceneX(), e.getSceneY());
            
            if (draggingPiece != null) {
                // 1. Always show the "True" valid move destination in green
                showValidMoves(); 
                
                // 2. If the user is hovering over the valid destination, make it brighter
                if (hoverSquare != null && isValidMove(draggingPiece, hoverSquare)) {
                    highlightCell(hoverSquare, true); // Strong Green
                }
            } else if (hoverSquare != null) {
                // 3. Just a normal hover when not dragging
                highlightCell(hoverSquare, false); // Light Yellow
            }
        });
    
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
        diceDisplay.setOnMouseClicked(e -> {
            if (!game.isGameOver() && !game.isMoveHasPending()) {
                performRoll();
            }
        });

    }

    private void rollDice() {
        if (game.isGameOver()) return;

        // 1️⃣ Roll dice for whoever clicked (always human first)
        int result = game.rollDice();
        updateDiceDisplay(result);
        refreshBoard();
        updateStatus();

        // 2️⃣ Handle AI only AFTER initial human roll
        if (isVsAI && !game.isNeedsInitialRoll() && game.getCurrentPlayer() != PlayerColor.WHITE) {
            // AI's turn
            Timeline aiDelay = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                ai.takeTurn();
                refreshBoard();
                updateStatus();

                // AI may have multiple moves due to carry-over
                if (!game.isGameOver() && game.getCurrentPlayer() == PlayerColor.BLACK) {
                    finishAITurn();
                }
            }));
            aiDelay.play();
        }
    }

    

    private void performRoll() {
        if (game.isGameOver()) return;

        int rollResult = game.rollDice();   // <-- handles initial roll internally
        updateDiceDisplay(rollResult);
        refreshBoard();
        updateStatus();

        // Check if AI needs to move
        if (isVsAI && game.getCurrentPlayer() == PlayerColor.BLACK) {
            finishAITurn(); // Auto-handle AI turn
        }
    }

    private void autoRollIfAITurn() {
        // Only run for AI
        if (!isVsAI || game.getCurrentPlayer() != PlayerColor.BLACK || game.isGameOver()) return;

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            performRoll(); // Roll dice for AI and trigger move
        }));
        delay.play();
    }

    private void finishAITurn() {
        if (game.isGameOver() || game.getCurrentPlayer() != PlayerColor.BLACK) return;

        // AI executes move
        Timeline aiMoveDelay = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            ai.takeTurn();
            refreshBoard();
            updateStatus();

            // If AI gets another move due to carry-over or roll-again
            if (!game.isGameOver() && game.getCurrentPlayer() == PlayerColor.BLACK) {
                finishAITurn(); // recursive call with delay
            } else {
                // Human turn: enable dice after AI finishes
                updateDiceInteractivity(true);
            }
        }));
        aiMoveDelay.play();
    }


    private void updateDiceDisplay(int rollResult) {
        Image whiteDice = new Image(getClass().getResource("/assets/senet/white_side_dice_stick.png").toExternalForm());
        Image darkDice = new Image(getClass().getResource("/assets/senet/dark_side_dice_stick.png").toExternalForm());

        for (int i = 0; i < 4; i++) {
            // SCENARIO: 4 White sides (Result is 4)
            if (rollResult == 4) {
                diceSticks[i].setImage(whiteDice);
            } 
            // SCENARIO: 0 White sides (Result is 5 or 6 depending on your logic)
            // If your SenetGame returns 5 or 6 for 'all dark', check for that here:
            else if (rollResult == 5 || rollResult == 6) { 
                diceSticks[i].setImage(darkDice);
            } 
            // SCENARIO: Normal 1, 2, or 3
            else {
                if (i < rollResult) {
                    diceSticks[i].setImage(whiteDice);
                } else {
                    diceSticks[i].setImage(darkDice);
                }
            }
        }
    }

    // ==================== INTERACTION ====================

    private void startDraggingPiece(SenetPiece piece, ImageView visual, MouseEvent evt) {
        if (!game.isMoveHasPending() || piece.getColor() != game.getCurrentPlayer()) return;
    
        // Guardrail: Block if no moves possible
        if (!game.canMoveForward(piece) && !game.canMoveBackward(piece)) {
            statusText.setText("❌ This piece has no valid moves!");
            statusText.setFill(Color.BLACK);
            return; 
        }
    
        draggingPiece = piece;
        draggingVisual = visual;
        statusText.setFill(Color.BLACK);
    
        // CREATE THE GHOST (dragProxy)
        // HIDE THE ORIGINAL PIECE while dragging
        draggingVisual.setOpacity(0.0);

        // Create the ghost (dragProxy)
        dragProxy = new ImageView(visual.getImage());
        double size = piece.getColor() == PlayerColor.WHITE ? WHITE_PIECE_SIZE : BLACK_PIECE_SIZE;
        dragProxy.setFitWidth(size);
        dragProxy.setFitHeight(size);
        dragProxy.setPreserveRatio(true);
        dragProxy.setOpacity(0.8);
        dragProxy.setMouseTransparent(true);
        dragProxy.setEffect(new DropShadow(15, Color.BLACK));
        
        this.getChildren().add(dragProxy);
        
        // Position it immediately
        dragProxy.setTranslateX(evt.getSceneX() - this.getWidth() / 2);
        dragProxy.setTranslateY(evt.getSceneY() - this.getHeight() / 2);
    
        // Highlight where you can actually go
        showValidMoves();
    }
    
    private void dragPiece(ImageView visual, MouseEvent evt) {
        if (dragProxy == null) return;
    
        // Move the ghost to follow cursor
        dragProxy.setTranslateX(evt.getSceneX() - this.getWidth() / 2);
        dragProxy.setTranslateY(evt.getSceneY() - this.getHeight() / 2);
    
        // Dynamic highlighting while dragging
        clearAllHighlights();
        showValidMoves(); // Keep valid moves green
    }
    
    private void finishDraggingPiece(ImageView visual, MouseEvent evt) {
        if (draggingPiece == null) return;

        // Remove ghost
        if (dragProxy != null) {
            this.getChildren().remove(dragProxy);
            dragProxy = null;
        }

        Integer targetSquare = findSquareAt(evt.getSceneX(), evt.getSceneY());

        boolean moved = false;

        if (targetSquare != null && isValidMove(draggingPiece, targetSquare)) {
            moved = game.movePiece(draggingPiece);
        }

        // Restore original piece if not moved
        if (!moved && draggingVisual != null) {
            draggingVisual.setOpacity(1.0);
        }

        // Cleanup
        clearAllHighlights();
        clearExitHighlight();

        draggingPiece = null;
        draggingVisual = null;

        refreshBoard();
        updateStatus();

        // ==== AUTOMATIC AI TURN ====
        if (isVsAI && !game.isGameOver() && game.getCurrentPlayer() == PlayerColor.BLACK) {
            // Delay a bit for visual effect
            Timeline aiDelay = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                finishAITurn(); // recursively handle AI moves if needed
            }));
            aiDelay.play();
        }
    }



    private Integer findSquareAt(double sceneX, double sceneY) {

    // ===== CHECK NORMAL BOARD CELLS (1–30) =====
    for (Map.Entry<Integer, StackPane> entry : cellMap.entrySet()) {
        StackPane cell = entry.getValue();
        Bounds bounds = cell.localToScene(cell.getBoundsInLocal());

        if (bounds.contains(sceneX, sceneY)) {
            return entry.getKey();
        }
    }

    // ===== CHECK EXIT CELL (31) =====
    if (exitCell != null) {
        Bounds exitBounds = exitCell.localToScene(exitCell.getBoundsInLocal());
        if (exitBounds.contains(sceneX, sceneY)) {
            return 31;
        }
    }

    return null;
}

    
    private void showValidMoves() {
        if (draggingPiece == null) return;

        int roll = game.getLastRoll();
        int pos = draggingPiece.getPosition();

        int forwardTarget = pos + roll;

        if (forwardTarget > 30 && game.canBearOff(draggingPiece)) {
            highlightExitCell();
        } 
        else if (game.canMoveForward(draggingPiece)) {
            highlightCell(forwardTarget, true);
        } 
        else if (game.canMoveBackward(draggingPiece)) {
            highlightCell(pos - roll, true);
        }
    }

    private void highlightExitCell() {
        Rectangle hl = (Rectangle) exitCell.lookup("#highlight");
        if (hl != null) hl.setVisible(true);
    }


    
    private boolean isValidMove(SenetPiece piece, int targetSquare) {
        if (piece == null) return false;

        int roll = game.getLastRoll();
        int from = piece.getPosition();

        if (targetSquare == 31) { // bearing off
            if (from + roll > 30) return game.canBearOff(piece);
            return false;
        }

        // Forward move has priority
        if (game.canMoveForward(piece)) {
            return targetSquare == from + roll;
        }

        // Only allow backward if forward is blocked
        if (!game.canMoveForward(piece) && game.canMoveBackward(piece)) {
            return targetSquare == from - roll;
        }

        return false;
    }




    // ==================== HIGHLIGHTING ====================

    private void highlightCell(int squareNum, boolean isValid) {
        StackPane cell = cellMap.get(squareNum);
        if (cell == null) return;
    
        // Look for the rectangle by the ID we set in buildBoard
        Rectangle hl = (Rectangle) cell.lookup("#highlight");
        if (hl != null) {
            hl.setVisible(true);
            // Green for valid moves, Yellow for general hovering
            hl.setFill(isValid ? Color.color(0, 1, 0, 0.4) : Color.color(1, 1, 0, 0.2));
        }
    }
    
    
    
    private void clearAllHighlights() {
        for (StackPane cell : cellMap.values()) {
            Rectangle hl = (Rectangle) cell.lookup("#highlight");
            if (hl != null) hl.setVisible(false);
        }
    }

    private void clearExitHighlight() {
        if (exitCell == null) return;

        Rectangle hl = (Rectangle) exitCell.lookup("#highlight");
        if (hl != null) {
            hl.setVisible(false);
        }
    }


    // ==================== REFRESH ====================

    private void refreshBoard() {
        // 1. Clear ONLY pieces (ImageViews that aren't the board background)
        for (StackPane cell : cellMap.values()) {
            // We remove any ImageView that is NOT the first child (the board square)
            cell.getChildren().removeIf(node -> node instanceof ImageView && cell.getChildren().indexOf(node) != 0);
        }
    
        // 2. Reposition all pieces
        for (Map.Entry<SenetPiece, ImageView> entry : pieceMap.entrySet()) {
            SenetPiece p = entry.getKey();
            ImageView view = entry.getValue();
    
            if (!p.isOffBoard()) {
                StackPane cell = cellMap.get(p.getPosition());
                if (cell == null) continue;
    
                view.setVisible(true);
                view.setOpacity(1.0);
                view.setTranslateX(0);
                view.setTranslateY(0);
    
                if (!cell.getChildren().contains(view)) {
                    cell.getChildren().add(view);
                }
                
                // 3. FORCE THE ORDER
                // Square at index 0, Piece in middle, Highlight on top
                cell.getChildren().stream()
                    .filter(n -> "highlight".equals(n.getId()))
                    .findFirst()
                    .ifPresent(javafx.scene.Node::toFront);
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
        // BEFORE dark-piece owner is determined → Player 1 / Player 2
        if (isVsAI) {
            // Human = WHITE, AI = BLACK
            if (game.getCurrentPlayer() == PlayerColor.WHITE) {
                playerText.setText("Your Turn");
            } else {
                playerText.setText("AI's Turn");
            }
        } else {
            // Normal two-player naming
            if (game.isNeedsInitialRoll()) {
                String playerName = game.getCurrentPlayer() == PlayerColor.WHITE ? "Player 1" : "Player 2";
                playerText.setText(playerName + "'s Turn");
            } else {
                String colorName = game.getCurrentPlayer() == PlayerColor.WHITE ? "White" : "Black";
                playerText.setText(colorName + "'s Turn");
            }
        }



        // Only enable dice when ready to roll (moveHasPending == false)
        if (!game.isMoveHasPending()) {
            updateDiceInteractivity(true);
        } else {
            // Block dice during move phase
            updateDiceInteractivity(false);
        }

        if (game.isGameOver()) {
            String winnerText;
            if (isVsAI) {
                winnerText = (game.getWinner() == PlayerColor.WHITE) ? "YOU WON!" : "AI WON!";
            } else {
                winnerText = (game.getWinner() == PlayerColor.WHITE) ? "WHITE WON!" : "BLACK WON!";
            }
            showGameOverOverlay(winnerText);
            updateDiceInteractivity(false);
        } else if (game.isNeedsInitialRoll()) {
            statusText.setText("Click the DICE to find dark piece owner!");
        } else if (game.isMoveHasPending()) {
            int roll = game.getLastRoll();
            String squareWord = roll > 1 ? "squares" : "square";
            statusText.setText("Move a piece " + roll + " " + squareWord + " (remainder applies if bearing off)");
        } else {
            String rollMsg = game.shouldRollAgain() ? "Roll Again!" : "Roll Dice";
            statusText.setText(rollMsg);
        }
    }

    private void showGameOverOverlay(String winnerName) {
        // 1. Create the darkened background "curtain"
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Darken screen
        overlay.setPrefSize(this.getWidth(), this.getHeight());

        // 2. Load the custom font
        Font headerFont = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 60);
        Font buttonFont = Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Cinzel-Medium.ttf"), 30);

        // 3. Header Text
        Text header = new Text(winnerName);
        header.setFont(headerFont);
        header.setFill(Color.web("#F5F1E6"));
        header.setEffect(new DropShadow(20, Color.BLACK));

        // 4. Buttons
        Button btnPlayAgain = new Button("PLAY AGAIN");
        Button btnBack = new Button("BACK");

        // Style buttons using your existing logic
        for (Button b : new Button[]{btnPlayAgain, btnBack}) {
            b.setFont(buttonFont);
            b.setTextFill(Color.web("#F5F1E6"));
            b.setStyle("-fx-background-color: rgba(139, 69, 19, 0.8); -fx-border-color: #F5F1E6; -fx-cursor: hand;");
            b.setPrefWidth(300);
            b.setOnMouseEntered(e -> b.setOpacity(0.8));
            b.setOnMouseExited(e -> b.setOpacity(1.0));
        }

        // 5. Button Actions
        btnPlayAgain.setOnAction(e -> {
            Stage stage = (Stage) this.getScene().getWindow();
            // Pass 'onBack' into the new screen so the next game also knows how to go back
            SenetScreen nextGame = new SenetScreen(stage, onBack); 
            
            if (this.isVsAI) {
                nextGame.setVsAI(true);
                nextGame.setAIDifficulty(SenetAI.Difficulty.MEDIUM); 
            }
            stage.getScene().setRoot(nextGame);
        });

        btnBack.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // 6. Layout
        VBox content = new VBox(30, header, btnPlayAgain, btnBack);
        content.setAlignment(Pos.CENTER);
        overlay.getChildren().add(content);

        // 7. Add to the main StackPane (this class)
        this.getChildren().add(overlay);
    }

    public void setVsAI(boolean vsAI) {
        this.isVsAI = vsAI;
    }

    public void setAIDifficulty(SenetAI.Difficulty difficulty) {
        if (isVsAI) {
            ai = new SenetAI(game, difficulty);
        }
    }

    private void createExitButton(Stage stage) {
        // 1. Load images
        Image bgImage = new Image(getClass().getResource("/assets/backgrounds/senet_background.jpeg").toExternalForm());
        Image crossImage = new Image(getClass().getResource("/assets/senet/red_cross.jpg").toExternalForm());

        // 2. Create the button container
        StackPane exitButton = new StackPane();
        exitButton.setMaxSize(50, 50); // Use setMaxSize for StackPane children
        exitButton.setPrefSize(50, 50);
        exitButton.setStyle("-fx-cursor: hand; -fx-background-radius: 5; -fx-overflow-hidden: true;");

        // 3. Create the background (clipped version of the game background)
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(50);
        bgView.setFitHeight(50);
        bgView.setPreserveRatio(false);
        
        // Add a small border/rect to make it stand out from the actual background
        Rectangle border = new Rectangle(50, 50);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);

        // 4. Create the Red Cross
        ImageView crossView = new ImageView(crossImage);
        crossView.setFitWidth(30);
        crossView.setFitHeight(30);
        crossView.setPreserveRatio(true);

        exitButton.getChildren().addAll(bgView, border, crossView);

        // 5. Layout Positioning: Top Right
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        exitButton.setTranslateX(-20); // Margin from right
        exitButton.setTranslateY(20);  // Margin from top

        // 6. Hover effects for better UX
        exitButton.setOnMouseEntered(e -> exitButton.setOpacity(0.8));
        exitButton.setOnMouseExited(e -> exitButton.setOpacity(1.0));

        // 7. Click logic to return to Menu
        exitButton.setOnMouseClicked(e -> {
            // 1. Create a reference for the mode screen so the game can come back here
            final GameModeScreen[] modeRef = new GameModeScreen[1];
            Runnable backToMode = () -> stage.getScene().setRoot(modeRef[0]);

            // 2. Define the selection actions
            Runnable onSingleplayer = () -> {
                // FIXED: Now passes stage AND the back runnable
                SenetScreen vsAI = new SenetScreen(stage, backToMode);
                vsAI.setVsAI(true);
                vsAI.setAIDifficulty(SenetAI.Difficulty.MEDIUM);
                stage.getScene().setRoot(vsAI);
            };

            Runnable onLocal2P = () -> {
                // FIXED: Now passes stage AND the back runnable
                stage.getScene().setRoot(new SenetScreen(stage, backToMode));
            };

            Runnable onBackToTitle = () -> {
                stage.getScene().setRoot(new TitleScreen(stage));
            };

            // 3. Create the mode screen and set it to the reference
            modeRef[0] = new GameModeScreen(
                "/assets/backgrounds/senet_background.jpeg",
                onSingleplayer,
                onLocal2P,
                onBackToTitle
            );

            // 4. Show the screen
            stage.getScene().setRoot(modeRef[0]);
        });

        // Add to the SenetScreen (this is a StackPane, so it adds to the top layer)
        this.getChildren().add(exitButton);
    }



}
