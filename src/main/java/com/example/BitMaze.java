package com.example;

import java.util.Arrays;

public class BitMaze {
    private final int[][] map;
    private final int width, height;
    public final static int LEFT = 0;
    public final static int RIGHT = 2;
    public final static int TOP = 3;
    public final static int BOTTOM = 1;

    public BitMaze(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                this.map[i][j] = 15;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] oth) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = oth[y][x];
            }
        }
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setMapForWallAdder() {
        for (int y = 0; y < height; y++) {
            Arrays.fill(map[y], 0);
            map[y][0] = 0b0001;
            map[y][width - 1] = 0b0100;
        }
        for (int x = 0; x < width; x++) {
            map[0][x] |= 0b1000;
            map[height - 1][x] |= 0b0010;
        }

    }
    public int getDir(Vertex curr, Vertex neighbor) {
        int dx = neighbor.x - curr.x;
        int dy = neighbor.y - curr.y;
        if (dx == 0)
            return dy == -1 ? 3 : 1;
        else
            return dx == -1 ? 0 : 2;
    }

    public boolean inbounds(Vertex v) {
        return v.x >= 0 && v.x < width && v.y >= 0 && v.y < height;
    }
    public boolean hasWall(Vertex curr, Vertex neighbor) {
        int wall = getDir(curr, neighbor);
        return (map[curr.y][curr.x] & 1 << wall) != 0;
    }
    public void setWall(Vertex curr, Vertex neighbor) {
        int idx = getDir(curr, neighbor);
        int oppIdx = idx < 2 ? idx + 2 : idx - 2;
        map[curr.y][curr.x] |= 1 << idx;
        map[neighbor.y][neighbor.x] |= 1 << oppIdx;
    }

    public void clearWall(Vertex curr, Vertex neighbor) {
        int idx = getDir(curr, neighbor);
        int oppIdx = idx < 2 ? idx + 2 : idx - 2;
        map[curr.y][curr.x] &= ~(1 << idx);
        map[neighbor.y][neighbor.x] &= ~(1 << oppIdx);
    }

    public void printMaze() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
