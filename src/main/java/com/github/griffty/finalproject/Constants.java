package com.github.griffty.finalproject;

public class Constants {
    public static final int TILE_SIZE = 128;
    static {
        if (TILE_SIZE % 2 != 0) {
            throw new IllegalStateException("TILE_SIZE must be even");
        }
    }

    public static final int START_MONEY = 100;
    public static final int START_HEALTH = 10;
}
