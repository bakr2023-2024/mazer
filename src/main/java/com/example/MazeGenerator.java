package com.example;

import java.util.Random;

public class MazeGenerator {
    private final boolean[][] map;
    private final int width, height;
    private final Random rand;
    public MazeGenerator(int width, int height, Generators alg) {
        this.width = width;
        this.height = height;
        this.map = new boolean[height][width];
        this.rand = new Random();
        switch (alg) {
            case RECURSIVE_BACKTRACKER:
                recursiveBacktracker();
                break;
            default:
                break;
        }
    }

    private void setMap() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                map[i][j] = false;
    }

    private void recursiveBacktracker() {
        setMap();

    }
}
