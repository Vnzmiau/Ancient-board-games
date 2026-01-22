package com.boardgames.games.senet;

import java.util.ArrayList;
import java.util.List;

/**
 * Complete implementation of Senet rules:
 * - 30 squares arranged in 3 rows of 10
 * - Square 27 is water (trap): redirects to square 15
 * - Squares 26, 28, 29 are safe
 * - Pieces cannot pass over or land on 3-piece blocks
 * - 2-piece groups are protected from attack
 * - 1st player to roll 1 takes dark pieces and rolls again
 * - Players roll 1, 4, or 6 to roll again; 2 or 3 ends turn
 * - Bearing off only when row 1 is clear
 */
public class SenetGame {

    private final SenetBoard board;
    private PlayerColor currentPlayer;
    private int lastRoll;
    private PlayerColor darkPieceOwner;
    private boolean gameStarted;
    private boolean needsInitialRoll;
    private boolean moveHasPending;  // Player hasn't executed move yet
    private boolean gameOver;
    private PlayerColor winner;

    public SenetGame() {
        board = new SenetBoard();
        currentPlayer = PlayerColor.WHITE;
        darkPieceOwner = null;
        gameStarted = false;
        needsInitialRoll = true;
        moveHasPending = false;
        gameOver = false;
        winner = null;
        lastRoll = 0;
        setupInitialPosition();
    }

    /**
     * Setup: alternating white and black pieces on squares 1-10,
     * with dark piece always on square 10.
     */
    private void setupInitialPosition() {
        for (int i = 1; i <= 10; i++) {
            PlayerColor color = (i % 2 == 1) ? PlayerColor.WHITE : PlayerColor.BLACK;
            if (i == 10) {
                color = PlayerColor.BLACK;
            }
            board.placePiece(new SenetPiece(color, i), i);
        }
    }

    /**
     * Roll the dice. During initial phase, keep rolling until someone
     * gets a 1 to claim dark pieces. During normal play, check if
     * a move is pending.
     */
    public int rollDice() {
        if (gameOver) {
            return lastRoll;
        }

        lastRoll = DiceSticks.roll();

        if (needsInitialRoll) {
            // During initial roll phase
            if (lastRoll == 1) {
                // This player gets dark pieces and the game officially starts
                initializeGameWithDarkPieces();
            } else {
                // Switch to other player, keep waiting for a 1
                currentPlayer = getOpponent(currentPlayer);
            }
        } else {
            // Normal gameplay: move is now pending
            moveHasPending = true;

            // Check if current player has any valid moves
            if (!hasAnyValidMove(currentPlayer)) {
                // No valid moves, must pass turn
                handleNoValidMove();
            }
        }

        return lastRoll;
    }

    /**
     * Initialize the game: award dark pieces to current player,
     * and move the dark piece on square 10 forward 1 square.
     */
    private void initializeGameWithDarkPieces() {
        PlayerColor initialRoller = currentPlayer;
        darkPieceOwner = initialRoller;
        gameStarted = true;
        needsInitialRoll = false;
        moveHasPending = true;

        // The player who wins the initial roll now controls the dark (black) pieces.
        // Switch the turn color to BLACK so they immediately move the dark piece.
        currentPlayer = PlayerColor.BLACK;

        // Move the dark piece on square 10 forward 1 square (roll was 1)
        SenetPiece darkOnTen = board.getPieceAt(10);
        if (darkOnTen != null && darkOnTen.getColor() == PlayerColor.BLACK) {
            lastRoll = 1;
            movePiece(darkOnTen);
            moveHasPending = false;

            // After moving, check if we roll again (rolled 1)
            if (!shouldRollAgain()) {
                currentPlayer = getOpponent(currentPlayer);
            }
        }
    }

    /**
     * Return true if rolling 1, 4, or 6 allows another roll.
     */
    public boolean shouldRollAgain() {
        return lastRoll == 1 || lastRoll == 4 || lastRoll == 6;
    }

    /**
     * Check if a specific piece can move forward by lastRoll.
     */
    public boolean canMoveForward(SenetPiece piece) {
        if (piece == null || piece.isOffBoard()) {
            return false;
        }

        int from = piece.getPosition();
        int to = from + lastRoll;

        // Can't move to or past your own pieces (unless bearing off)
        if (to <= 30 && board.isOccupiedByFriendly(to, piece.getColor())) {
            return false;
        }

        // Check for opponent block between from and to
        if (board.hasOpponentBlockBetween(from, to, getOpponent(piece.getColor()))) {
            return false;
        }

        // If landing on opponent piece, check if protected
        if (board.isOccupied(to) && to <= 30) {
            SenetPiece target = board.getPieceAt(to);
            if (target != null && target.getColor() != piece.getColor()) {
                // Can't capture on safe squares
                if (board.isSafeSquare(to)) {
                    return false;
                }
                // Can't capture protected groups of 2+
                if (board.isProtectedGroup(to)) {
                    return false;
                }
            }
        }

        // Check bearing-off rules
        if (to > 30) {
            return canBearOff(piece);
        }

        return true;
    }

    /**
     * Check if a piece can move backward (when unable to move forward).
     * Backward movement is still constrained by the same rules.
     */
    public boolean canMoveBackward(SenetPiece piece) {
        if (piece == null || piece.isOffBoard() || piece.getPosition() == 1) {
            return false;
        }

        // Find a valid backward destination
        int from = piece.getPosition();
        int to = from - lastRoll;

        if (to < 1) {
            return false;
        }

        // Can't move to your own pieces
        if (board.isOccupiedByFriendly(to, piece.getColor())) {
            return false;
        }

        // If landing on opponent, check if protected
        if (board.isOccupied(to)) {
            SenetPiece target = board.getPieceAt(to);
            if (target != null && target.getColor() != piece.getColor()) {
                if (board.isSafeSquare(to) || board.isProtectedGroup(to)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if player has ANY valid move (forward or backward).
     */
    public boolean hasAnyValidMove(PlayerColor color) {
        for (int i = 1; i <= 30; i++) {
            SenetPiece p = board.getPieceAt(i);
            if (p != null && p.getColor() == color && !p.isOffBoard()) {
                if (canMoveForward(p) || canMoveBackward(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if player has ANY valid forward move.
     */
    public boolean hasAnyValidMoveForward(PlayerColor color) {
        for (int i = 1; i <= 30; i++) {
            SenetPiece p = board.getPieceAt(i);
            if (p != null && p.getColor() == color && !p.isOffBoard()) {
                if (canMoveForward(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handle the case where player has no valid moves.
     */
    private void handleNoValidMove() {
        moveHasPending = false;

        // After no valid move, switch turns
        if (!shouldRollAgain()) {
            currentPlayer = getOpponent(currentPlayer);
        }
    }

    /**
     * Execute the move of a piece forward or backward by lastRoll.
     * Returns true if move was successful.
     * Rules: Always try forward first. Only try backward if forward is blocked.
     */
    public boolean movePiece(SenetPiece piece) {
        if (piece == null || piece.isOffBoard()) {
            return false;
        }

        int from = piece.getPosition();
        int toForward = from + lastRoll;
        int toBackward = from - lastRoll;

        // Try forward first
        if (toForward >= 1 && canMoveForward(piece)) {
            return executeMoveTo(piece, toForward);
        }

        // Try backward only if forward blocked
        if (toBackward >= 1 && canMoveBackward(piece)) {
            return executeMoveTo(piece, toBackward);
        }

        // No valid move - piece is stuck
        moveHasPending = false;
        return false;
    }

    /**
     * Execute the actual move logic: handle traps, captures, bearing off.
     */
    private boolean executeMoveTo(SenetPiece piece, int to) {
        int from = piece.getPosition();
        board.removePiece(from);

        if (to > 30) {
            // Bearing off
            if (canBearOff(piece)) {
                piece.setPosition(-1);
                moveHasPending = false;
                checkWinCondition();
                handleTurnEnd();
                return true;
            } else {
                // Can't bear off, revert
                board.placePiece(piece, from);
                return false;
            }
        }

        // Handle water trap (square 27 -> square 15 or nearest available)
        if (to == 27) {
            to = 15;
            while (to > 1 && board.isOccupied(to)) {
                to--;
            }
        }

        // Handle capture
        if (board.isOccupied(to)) {
            SenetPiece enemy = board.getPieceAt(to);
            if (enemy != null && enemy.getColor() != piece.getColor()
                    && !board.isSafeSquare(to)
                    && !board.isProtectedGroup(to)) {
                // Capture: swap positions
                board.removePiece(to);
                board.placePiece(enemy, from);
            }
        }

        piece.setPosition(to);
        board.placePiece(piece, to);
        moveHasPending = false;
        handleTurnEnd();
        return true;
    }

    /**
     * Handle turn end: check if should roll again or switch players.
     */
    private void handleTurnEnd() {
        if (!shouldRollAgain()) {
            currentPlayer = getOpponent(currentPlayer);
        }
    }

    /**
     * Check if bearing off is allowed (all pieces in row 1 must be gone).
     */
    public boolean canBearOff(SenetPiece piece) {
        if (piece == null || piece.isOffBoard()) {
            return false;
        }

        // Can only bear off if no pieces in row 1 (squares 1-10)
        for (int i = 1; i <= 10; i++) {
            SenetPiece p = board.getPieceAt(i);
            if (p != null && p.getColor() == piece.getColor()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if player has won.
     */
    private void checkWinCondition() {
        // Check if current player has all pieces off board
        boolean allOff = true;
        for (int i = 1; i <= 30; i++) {
            SenetPiece p = board.getPieceAt(i);
            if (p != null && p.getColor() == currentPlayer) {
                allOff = false;
                break;
            }
        }

        if (allOff) {
            gameOver = true;
            winner = currentPlayer;
        }
    }

    /**
     * Get list of pieces the player can currently move.
     */
    public List<SenetPiece> getValidPieces(PlayerColor color) {
        List<SenetPiece> valid = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            SenetPiece p = board.getPieceAt(i);
            if (p != null && p.getColor() == color && !p.isOffBoard()) {
                if (canMoveForward(p) || canMoveBackward(p)) {
                    valid.add(p);
                }
            }
        }
        return valid;
    }

    // ===== Getters =====

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public SenetBoard getBoard() {
        return board;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isNeedsInitialRoll() {
        return needsInitialRoll;
    }

    public PlayerColor getDarkPieceOwner() {
        return darkPieceOwner;
    }

    public boolean isMoveHasPending() {
        return moveHasPending;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public static PlayerColor getOpponent(PlayerColor color) {
        return color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }
}
