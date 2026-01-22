package com.boardgames.games.senet;

import java.util.Random;

public class DiceSticks {

    private static final Random random = new Random();

    public static int roll() {
        int flatUp = 0;

        for (int i = 0; i < 4; i++) {
            if (random.nextBoolean()) {
                flatUp++;
            }
        }

        // all rounded sides up = 6
        return flatUp == 0 ? 6 : flatUp;
    }
}
