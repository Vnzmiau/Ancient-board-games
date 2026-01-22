package com.boardgames.games.senet;

public class SenetPiece {

    private final PlayerColor color;
    private int position; // 1–30, -1 = borne off

    public SenetPiece(PlayerColor color, int position) {
        this.color = color;
        this.position = position;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isOffBoard() {
        return position == -1;
    }
}
