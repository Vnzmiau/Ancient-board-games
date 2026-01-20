package com.boardgames.ui.screens;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SenetScreen extends StackPane {

    // ====== MANUAL TUNING VALUES YOU CAN CHANGE ======

    // board size
    private static final double BOARD_WIDTH = 900;

    // piece sizes
    private static final double WHITE_PIECE_SIZE = 90;
    private static final double BLACK_PIECE_SIZE = 60;

    // board layout constants
    private static final double BOARD_OUTER_BORDER = 16;  // px border around entire board
    private static final double CELL_BORDER = 1;          // px border between adjacent cells (reduced for closer spacing)

    // piece positioning adjustments (adjust these to position pieces on X-axis)
    private static final double FIRST_WHITE_PIECE_X_OFFSET = 43;  // X offset for first white piece
    private static final double FIRST_BLACK_PIECE_X_OFFSET = 34;  // X offset for first black piece
    private static final double WHITE_PIECE_SPACING = 1.77;         // cell spacing between white pieces (currently 2 = every other cell)
    private static final double BLACK_PIECE_SPACING = 1.77;         // cell spacing between black pieces (currently 2 = every other cell)
    private static final double BLACK_FROM_WHITE_OFFSET = 1;     // cell offset of black pieces from white pieces (currently 1)

    // dice size & spacing
    private static final double DICE_SIZE = 150;
    private static final double SPACING_BETWEEN_ITEMS = 100;      // spacing between board and dice
    private static final double VERTICAL_CENTER_OFFSET = -900;    // offset from center (negative = move up, positive = move down)

    public SenetScreen(Stage stage) {

        // ===== Background =====
        Image bg = new Image(
                getClass().getResource("/assets/backgrounds/senet_background.jpeg").toExternalForm()
        );
        ImageView bgView = new ImageView(bg);
        bgView.setPreserveRatio(false);

        // ===== Board =====
        Image boardImg = new Image(
                getClass().getResource("/assets/senet/senet_board.png").toExternalForm()
        );
        ImageView board = new ImageView(boardImg);
        board.setFitWidth(BOARD_WIDTH);
        board.setPreserveRatio(true);
        
        // Calculate board height based on image aspect ratio
        double boardHeight = (BOARD_WIDTH / boardImg.getWidth()) * boardImg.getHeight();

        // ===== Piece images =====
        Image whitePieceImg = new Image(
                getClass().getResource("/assets/senet/white_piece.png").toExternalForm()
        );
        Image darkPieceImg = new Image(
                getClass().getResource("/assets/senet/dark_piece.png").toExternalForm()
        );

        // ===== Dice images =====
        Image diceWhite = new Image(
                getClass().getResource("/assets/senet/white_side_dice_stick.png").toExternalForm()
        );
        Image diceDark = new Image(
                getClass().getResource("/assets/senet/dark_side_dice_stick.png").toExternalForm()
        );

        // === Dice row ===
        HBox diceRow = new HBox(12);
        diceRow.setAlignment(Pos.CENTER);

        ImageView d1 = new ImageView(diceWhite);
        ImageView d2 = new ImageView(diceDark);
        ImageView d3 = new ImageView(diceWhite);
        ImageView d4 = new ImageView(diceDark);

        d1.setFitHeight(DICE_SIZE); d1.setPreserveRatio(true);
        d2.setFitHeight(DICE_SIZE); d2.setPreserveRatio(true);
        d3.setFitHeight(DICE_SIZE); d3.setPreserveRatio(true);
        d4.setFitHeight(DICE_SIZE); d4.setPreserveRatio(true);

        diceRow.getChildren().addAll(d1, d2, d3, d4);

        // === Piece overlay ===
        Pane pieceOverlay = new Pane();
        pieceOverlay.setPrefWidth(BOARD_WIDTH);
        pieceOverlay.setPrefHeight(boardHeight);

        // Calculate square dimensions based on board layout:
        // BOARD_WIDTH = 900px total
        // Outer borders: 16px left + 16px right = 32px
        // 10 cells with 9 borders between them: 9 * 1px = 9px
        // Remaining space for cells: 900 - 32 - 9 = 859px
        // Each cell width: 859 / 10 = 85.9px
        double cellWidth = (BOARD_WIDTH - 2 * BOARD_OUTER_BORDER - 9 * CELL_BORDER) / 10;
        
        // Board has 3 rows; pieces go on TOP row
        // Calculate row height (boardHeight / 3 for 3 rows)
        double rowHeight = boardHeight / 3;
        
        // Baseline Y position: pieces align to bottom of row
        // Largest piece (white = 90px) will have its bottom at this baseline
        double baselineY = BOARD_OUTER_BORDER + rowHeight - (WHITE_PIECE_SIZE / 2);

        // Store references to first white and first black pieces for mouse drag
        ImageView[] firstWhitePiece = new ImageView[1];
        ImageView[] firstBlackPiece = new ImageView[1];
        double[] dragOffsetX = new double[1];

        // Create 5 white pieces evenly spaced (every 2 cells)
        for (int w = 0; w < 5; w++) {
            ImageView whitePiece = new ImageView(whitePieceImg);
            whitePiece.setFitWidth(WHITE_PIECE_SIZE);
            whitePiece.setPreserveRatio(true);

            double cellIndex = w * WHITE_PIECE_SPACING;  // spacing controlled by WHITE_PIECE_SPACING
            double cellStartX = BOARD_OUTER_BORDER + cellIndex * (cellWidth + CELL_BORDER);
            double cellCenterX = cellStartX + cellWidth / 2;

            // Apply X offset for first white piece, then maintain spacing for others
            double xPos = cellCenterX - WHITE_PIECE_SIZE / 2;
            if (w == 0) {
                xPos += FIRST_WHITE_PIECE_X_OFFSET;
                firstWhitePiece[0] = whitePiece;
            } else if (w > 0) {
                // Position relative to first white piece, maintaining spacing
                double firstWhiteX = (BOARD_OUTER_BORDER + cellWidth / 2) - WHITE_PIECE_SIZE / 2 + FIRST_WHITE_PIECE_X_OFFSET;
                double spacingBetweenWhites = WHITE_PIECE_SPACING * (cellWidth + CELL_BORDER);
                xPos = firstWhiteX + w * spacingBetweenWhites;
            }

            whitePiece.setLayoutX(xPos);
            whitePiece.setLayoutY(baselineY - WHITE_PIECE_SIZE / 2);

            pieceOverlay.getChildren().add(whitePiece);
        }

        // Create 5 black pieces evenly spaced (offset by BLACK_FROM_WHITE_OFFSET)
        for (int b = 0; b < 5; b++) {
            ImageView blackPiece = new ImageView(darkPieceImg);
            blackPiece.setFitWidth(BLACK_PIECE_SIZE);
            blackPiece.setPreserveRatio(true);

            double cellIndex = b * BLACK_PIECE_SPACING + BLACK_FROM_WHITE_OFFSET;  // offset and spacing controlled
            double cellStartX = BOARD_OUTER_BORDER + cellIndex * (cellWidth + CELL_BORDER);
            double cellCenterX = cellStartX + cellWidth / 2;

            // Apply X offset for first black piece, then maintain spacing for others
            double xPos = cellCenterX - BLACK_PIECE_SIZE / 2;
            if (b == 0) {
                xPos += FIRST_BLACK_PIECE_X_OFFSET;
                firstBlackPiece[0] = blackPiece;
            } else if (b > 0) {
                // Position relative to first black piece, maintaining spacing
                double firstBlackX = (BOARD_OUTER_BORDER + BLACK_FROM_WHITE_OFFSET * (cellWidth + CELL_BORDER) + cellWidth / 2) - BLACK_PIECE_SIZE / 2 + FIRST_BLACK_PIECE_X_OFFSET;
                double spacingBetweenBlacks = BLACK_PIECE_SPACING * (cellWidth + CELL_BORDER);
                xPos = firstBlackX + b * spacingBetweenBlacks;
            }

            blackPiece.setLayoutX(xPos);
            blackPiece.setLayoutY(baselineY - BLACK_PIECE_SIZE / 2);

            pieceOverlay.getChildren().add(blackPiece);
        }

        // ==== stack board + pieces ====
        StackPane boardStack = new StackPane(board, pieceOverlay);
        boardStack.setMaxWidth(BOARD_WIDTH);

        // === Main layout: board and dice centered with offset ===
        VBox main = new VBox(SPACING_BETWEEN_ITEMS);
        main.setAlignment(Pos.CENTER);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        
        main.getChildren().addAll(spacer1, boardStack, diceRow, spacer2);
        
        // Both spacers grow equally to center the content
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        
        // Apply vertical offset: negative = move up, positive = move down
        // Adjust top spacer's growth to shift both items
        main.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Calculate offset by adjusting the top spacer
                spacer1.setPrefHeight(newVal.doubleValue() / 2 + VERTICAL_CENTER_OFFSET);
            }
        });

        // === add everything ===
        getChildren().addAll(bgView, main);

        // fullscreen background bind
        widthProperty().addListener((obs, o, n) -> bgView.setFitWidth(n.doubleValue()));
        heightProperty().addListener((obs, o, n) -> bgView.setFitHeight(n.doubleValue()));
    }
}
