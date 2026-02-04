package com.example;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private final BitMaze map;
    private final int width, height;
    private final Random r;
    public MazeGenerator(int width, int height, Generators alg) {
        this.width = width;
        this.height = height;
        this.map = new BitMaze(width, height);
        this.r = new Random();
        switch (alg) {
            case RECURSIVE_BACKTRACKER:
                recursiveBacktracker();
                break;
            default:
                break;
        }
    }

    public void printMap() {
        map.printMaze();
    }

    public boolean inBounds(Vertex v) {
        return v.x >= 0 && v.x < width && v.y >= 0 && v.y < height;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public BitMaze getMap() {
        return map;
    }

    private void recursiveBacktracker() {
        Vertex curr = Utils.getRandomVtx(width, height);
        Stack<Vertex> stack = new Stack<>();
        HashSet<Vertex> visited = new HashSet<>();
        stack.add(curr);
        while (!stack.isEmpty()) {
            curr = stack.peek();
            visited.add(curr);
            var neighbor = Utils.getRandomVtx(curr.getNeighbors(v -> inBounds(v) && !visited.contains(v)));
            if (neighbor != null) {
                map.clearWall(curr, neighbor);
                stack.add(neighbor);
            } else
                stack.pop();
        }
    }
private void kruskal(){
    
}
}
