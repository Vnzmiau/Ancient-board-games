package com.boardgames.games.senet;

import java.util.HashMap;
import java.util.Map;

/**
 * Senet board state management.
 * Handles piece placement, validation, and board rules.
 */
public class SenetBoard {

    private final Map<Integer, SenetPiece> board = new HashMap<>();

    public boolean isOccupied(int position) {
        return board.containsKey(position);
    }

    public SenetPiece getPieceAt(int position) {
        return board.get(position);
    }

    public void placePiece(SenetPiece piece, int position) {
        board.put(position, piece);
        piece.setPosition(position);
    }

    public void removePiece(int position) {
        board.remove(position);
    }

    /**
     * Check if a position is occupied by a friendly piece (same color).
     */
    public boolean isOccupiedByFriendly(int position, PlayerColor color) {
        SenetPiece p = board.get(position);
        return p != null && p.getColor() == color;
    }

    /**
     * Check if position is a safe square (26, 28, or 29).
     * Pieces on safe squares cannot be attacked.
     */
    public boolean isSafeSquare(int position) {
        return position == 26 || position == 28 || position == 29;
    }

    /**
     * Check if position is the water trap (27).
     */
    public boolean isWaterSquare(int position) {
        return position == 27;
    }

    /**
     * Check if there is a 3-piece block (same color) at the given position.
     * A block consists of 3 consecutive pieces of the same color starting at position.
     */
    public boolean isBlockAt(int position, PlayerColor color) {
        if (position < 1 || position > 28) {  // Block can't start after position 28
            return false;
        }
        int count = 0;
        for (int i = position; i <= position + 2 && i <= 30; i++) {
            SenetPiece p = board.get(i);
            if (p != null && p.getColor() == color) {
                count++;
            }
        }
        return count >= 3;
    }

    /**
     * Check if the piece at the given position has an adjacent friendly piece.
     * Two pieces of the same color on consecutive squares protect each other from attack.
     */
    public boolean isProtectedGroup(int position) {
        SenetPiece center = board.get(position);
        if (center == null) {
            return false;
        }
        PlayerColor color = center.getColor();

        // Check left neighbor
        SenetPiece left = board.get(position - 1);
        if (left != null && left.getColor() == color) {
            return true;
        }

        // Check right neighbor
        SenetPiece right = board.get(position + 1);
        return right != null && right.getColor() == color;
    }

    /**
     * Check if there is a 3-piece opponent block between from (exclusive) and to (inclusive).
     * This prevents a piece from moving over or landing on an opponent's block of 3.
     */
    public boolean hasOpponentBlockBetween(int from, int to, PlayerColor opponentColor) {
        if (to <= from) {
            return false;
        }
        for (int pos = from + 1; pos <= to; pos++) {
            if (isBlockAt(pos, opponentColor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the entire board state.
     */
    public Map<Integer, SenetPiece> getBoardState() {
        return new HashMap<>(board);
    }

    /**
     * Count remaining pieces for a player (not yet borne off).
     */
    public int countPiecesOnBoard(PlayerColor color) {
        int count = 0;
        for (int i = 1; i <= 30; i++) {
            SenetPiece p = board.get(i);
            if (p != null && p.getColor() == color) {
                count++;
            }
        }
        return count;
    }
}
