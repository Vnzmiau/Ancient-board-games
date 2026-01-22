package com.boardgames.games.senet;

import java.util.List;
import java.util.Random;

/**
 * AI for Senet game
 */
public class SenetAI {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private final SenetGame game;
    private final Difficulty difficulty;
    private final Random rand = new Random();

    public SenetAI(SenetGame game, Difficulty difficulty) {
        this.game = game;
        this.difficulty = difficulty;
    }

    /**
     * Take a turn as AI: roll dice, move a piece.
     * Always assumes AI is BLACK.
     */
    public void takeTurn() {
        if (game.isGameOver() || game.getCurrentPlayer() != PlayerColor.BLACK) return;

        // Roll dice
        int roll = game.rollDice();

        // Wait until move is pending
        if (!game.isMoveHasPending()) return;

        // Pick piece to move based on difficulty
        List<SenetPiece> validPieces = game.getValidPieces(PlayerColor.BLACK);

        if (validPieces.isEmpty()) {
            game.skipTurn();
            return;
        }

        SenetPiece selectedPiece = null;

        switch (difficulty) {
            case EASY -> {
                // Random move
                selectedPiece = validPieces.get(rand.nextInt(validPieces.size()));
            }
            case MEDIUM -> {
                // Move piece closest to exit
                selectedPiece = validPieces.stream()
                        .max((p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()))
                        .orElse(validPieces.get(0));
            }
            case HARD -> {
                // Move piece that maximizes landing on opponent or bearing off
                selectedPiece = validPieces.get(0);
                int bestScore = -1;
                for (SenetPiece p : validPieces) {
                    int target = p.getPosition() + game.getLastRoll();
                    int score = 0;
                    if (target > 30) score += 10; // bearing off
                    if (target <= 30 && game.getBoard().isOccupied(target)) {
                        SenetPiece enemy = game.getBoard().getPieceAt(target);
                        if (enemy != null && enemy.getColor() == PlayerColor.WHITE) score += 5;
                    }
                    if (score > bestScore) {
                        bestScore = score;
                        selectedPiece = p;
                    }
                }
            }
        }

        if (selectedPiece != null) {
            game.movePiece(selectedPiece);

            // If carryOverRoll exists, recursively move
            if (game.getLastRoll() > 0 && game.isMoveHasPending()) {
                takeTurn();
            }
        }
    }
}
