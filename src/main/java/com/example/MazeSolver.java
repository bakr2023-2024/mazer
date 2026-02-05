package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class MazeSolver {
    public List<Vertex> solve(BitMaze maze, Vertex start, Vertex end, Solvers alg) {
        switch (alg) {
            case DFS:
                return dfs(maze, start, end);
            default:
                return null;
        }
    }

    private List<Vertex> constructPath(HashMap<Vertex, Pair> path, Vertex start, Vertex end) {
        List<Vertex> solution = new ArrayList<>();
        Vertex curr = end;
        while (!curr.equals(start)) {
            solution.add(curr);
            curr = path.get(curr).getPrev();
        }
        solution.add(curr);
        return solution.reversed();
    }

    private void relaxEdge(HashMap<Vertex, Pair> path, Vertex curr, Vertex prev) {
        if (path.get(curr) == null || path.get(curr).getDistance() > path.get(prev).getDistance() + 1)
            path.put(curr, new Pair(prev, path.get(prev).getDistance() + 1));
    }

}
