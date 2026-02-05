package com.example;

import java.util.ArrayList;

public class MazeSolver {
    public ArrayList<Vertex> solve(BitMaze maze, Vertex start, Vertex end, Solvers alg) {
        switch (alg) {
            case DFS:
                return dfs(maze, start, end);
        }
        return new ArrayList<>();
    }

    private ArrayList<Vertex> dfs(BitMaze maze, Vertex start, Vertex end) {
        return null;
    }
}
