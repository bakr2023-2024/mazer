package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.function.ToIntFunction;

public class MazeSolver {
    public List<Vertex> solve(BitMaze maze, Vertex start, Vertex end, Solvers alg) {
        switch (alg) {
            case DFS:
                return dfs(maze, start, end);
            case BFS:
                return bfs(maze, start, end);
            case ASTAR:
                return aStar(maze, start, end);
            case BEST:
                return best(maze, start, end);
            case TREMAUX:
                return tremaux(maze, start, end);
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

    private List<Vertex> bfs(BitMaze maze, Vertex start, Vertex end) {
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(start);
        HashSet<Vertex> visited = new HashSet<>();
        HashMap<Vertex, Pair> path = new HashMap<>();
        path.put(start, new Pair(start, 0));
        while (!queue.isEmpty()) {
            Vertex curr = queue.poll();
            visited.add(curr);
            if (curr.equals(end))
                return constructPath(path, start, end);
            curr.getNeighbors(v -> maze.inbounds(v) && !visited.contains(v) && !maze.hasWall(v, curr)).forEach(n -> {
                visited.add(n);
                queue.add(n);
                relaxEdge(path, n, curr);
            });
        }
        return null;
    }

    private List<Vertex> aStar(BitMaze maze, Vertex start, Vertex end) {
        HashMap<Vertex, Pair> path = new HashMap<>();
        path.put(start, new Pair(start, 0));
        HashMap<Vertex, Integer> heuristic = new HashMap<>();
        ToIntFunction<Vertex> fCost = v -> heuristic.get(v) + path.get(v).getDistance();
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                Vertex curr = new Vertex(x, y);
                heuristic.put(curr, Math.abs(curr.x - end.x) + Math.abs(curr.y - end.y));
            }
        }
        HashSet<Vertex> visited = new HashSet<>();
        PriorityQueue<Vertex> pq = new PriorityQueue<>((a, b) -> {
            int aF = fCost.applyAsInt(a), bF = fCost.applyAsInt(b);
            return aF == bF ? heuristic.get(a) - heuristic.get(b) : aF - bF;
        });
        pq.add(start);
        while (!pq.isEmpty()) {
            Vertex curr = pq.poll();
            visited.add(curr);
            if (curr.equals(end))
                return constructPath(path, start, end);
            curr.getNeighbors(v -> maze.inbounds(v) && !visited.contains(v) && !maze.hasWall(curr, v)).forEach(n -> {
                relaxEdge(path, n, curr);
                visited.add(n);
                pq.add(n);
            });
        }
        return null;
    }

    private List<Vertex> best(BitMaze maze, Vertex start, Vertex end) {
        HashMap<Vertex, Pair> path = new HashMap<>();
        path.put(start, new Pair(start, 0));
        HashMap<Vertex, Integer> heuristic = new HashMap<>();
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                Vertex curr = new Vertex(x, y);
                heuristic.put(curr, Math.abs(curr.x - end.x) + Math.abs(curr.y - end.y));
            }
        }
        HashSet<Vertex> visited = new HashSet<>();
        PriorityQueue<Vertex> pq = new PriorityQueue<>((a, b) -> heuristic.get(a) - heuristic.get(b));
        pq.add(start);
        while (!pq.isEmpty()) {
            Vertex curr = pq.poll();
            visited.add(curr);
            if (curr.equals(end))
                return constructPath(path, start, end);
            curr.getNeighbors(v -> maze.inbounds(v) && !visited.contains(v) && !maze.hasWall(curr, v)).forEach(n -> {
                relaxEdge(path, n, curr);
                visited.add(n);
                pq.add(n);
            });
        }
        return null;
    }

    private List<Vertex> tremaux(BitMaze maze, Vertex start, Vertex end) {
        HashMap<Edge, Integer> marks = new HashMap<>();
        HashMap<Vertex, Pair> path = new HashMap<>();
        path.put(start, new Pair(start, 0));
        Vertex curr = start;
        Vertex prev = start;
        while (!curr.equals(end)) {
            final Vertex fPrev = prev;
            final Vertex fCurr = curr;
            var neighbors = curr.getNeighbors(
                    v -> maze.inbounds(v) && marks.get(new Edge(v, fCurr)) == null && !maze.hasWall(fCurr, v));
            Vertex newNeighbor = Utils.getRandomVtx(neighbors);
            Edge prevEdge = new Edge(prev, curr);
            if (newNeighbor != null) {
                prevEdge = new Edge(curr, newNeighbor);
                prev = curr;
                curr = newNeighbor;
            } else if (marks.get(prevEdge) < 2) {
                Vertex temp = curr;
                curr = prev;
                prev = temp;
            } else {
                var neighbor = Utils.getRandomVtx(
                        curr.getNeighbors(v -> maze.inbounds(v) && !v.equals(fPrev) && !maze.hasWall(fCurr, v)
                                && marks.get(new Edge(fCurr, v)) < 2));
                prevEdge = new Edge(curr, neighbor);
                prev = curr;
                curr = neighbor;
            }
            marks.put(prevEdge, marks.getOrDefault(prevEdge, 0) + 1);
            relaxEdge(path, curr, prev);
        }
        return constructPath(path, start, end);
    }
}
