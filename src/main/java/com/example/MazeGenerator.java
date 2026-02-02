package com.example;

public class MazeGenerator {
    private boolean[][] map;
    private int width, height;

    public MazeGenerator(int width, int height, Generators alg) {
        this.width = width;
        this.height = height;
        map = new boolean[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                map[i][j] = false;
        
    }
}
