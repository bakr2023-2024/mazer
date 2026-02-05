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

    private List<Vertex> dfs(BitMaze maze, Vertex start, Vertex end) {
        Stack<Vertex> stack = new Stack<>();
        HashSet<Vertex> visited = new HashSet<>();
        HashMap<Vertex, Pair> path = new HashMap<>();
        path.put(start, new Pair(start, 0));
        stack.add(start);
        while (!stack.isEmpty()) {
            Vertex curr = stack.peek();
            visited.add(curr);
            if (curr.equals(end))
                return constructPath(path, start, end);
            var neighbor = Utils.getRandomVtx(
                    curr.getNeighbors(v -> maze.inbounds(v) && !visited.contains(v) && !maze.hasWall(curr, v)));
            if (neighbor != null) {
                stack.add(neighbor);
                relaxEdge(path, neighbor, curr);
            } else
                stack.pop();
        }
        return null;
    }

}
