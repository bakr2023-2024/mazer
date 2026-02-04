package com.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
            case KRUSKAL:
                kruskal();
                break;
            case PRIM:
                prim();
                break;
            case ALDOUS_BRODER:
                aldousBroder();
                break;
            case WILSON:
                wilson();
                break;
            case HUNT_AND_KILL:
                huntAndKill();
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

    private List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vertex curr = new Vertex(x, y);
                if (y < height - 1)
                    edges.add(new Edge(curr, curr.add(0, 1)));
                if (x < width - 1)
                    edges.add(new Edge(curr, curr.add(1, 0)));
            }
        }
        return edges;
    }

    private List<Vertex> getVertices() {
        List<Vertex> vertices = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                vertices.add(new Vertex(x, y));
            }
        }
        return vertices;
    }

    private void kruskal() {
        List<Edge> edges = getEdges();
        Collections.shuffle(edges);
        DisjointSet ds = new DisjointSet(width, height);
        for (Edge e : edges) {
            if (ds.union(e.getV1(), e.getV2()))
                map.clearWall(e.getV1(), e.getV2());
        }
}

private void prim() {
    var vertices = getVertices();
    Vertex start = vertices.remove(r.nextInt(vertices.size()));
    HashSet<Vertex> in = new HashSet<>();
    in.add(start);
    List<Vertex> frontier = new ArrayList<>(start.getNeighbors(v -> inBounds(v)));
    while (!frontier.isEmpty()) {
        Vertex curr = frontier.remove(r.nextInt(frontier.size()));
        var neighbors = curr.getNeighbors(v -> inBounds(v) && !frontier.contains(v));
        Collections.shuffle(neighbors);
        List<Vertex> inNeighbors = new ArrayList<>();
        for (Vertex n : neighbors) {
            if (in.contains(n))
                inNeighbors.add(n);
            else
                frontier.add(n);
        }
        if (!inNeighbors.isEmpty())
            map.clearWall(curr, inNeighbors.get(0));
        in.add(curr);
    }
}

private void aldousBroder() {
    var vertices = getVertices();
    HashSet<Vertex> visited = new HashSet<>();
    Vertex curr = vertices.remove(r.nextInt(vertices.size()));
    visited.add(curr);
    int n = width * height;
    while (visited.size() < n) {
        var neighbor = Utils.getRandomVtx(curr.getNeighbors(v -> inBounds(v)));
        if (!visited.contains(neighbor)) {
            map.clearWall(curr, neighbor);
            visited.add(neighbor);
        }
        curr = neighbor;
    }
}

private void wilson() {
    var vertices = getVertices();
    Collections.shuffle(vertices);
    HashSet<Vertex> ust = new HashSet<>();
    ust.add(vertices.remove(r.nextInt(vertices.size())));
    HashMap<Vertex, Vertex> path = new HashMap<>();
    int n = width * height;
    while (ust.size() < n) {
        Vertex curr = vertices.remove(r.nextInt(vertices.size()));
        Vertex start = curr;
        while (!ust.contains(curr)) {
            var next = Utils.getRandomVtx(curr.getNeighbors(v -> inBounds(v)));
            path.put(curr, next);
            curr = next;
        }
        while (!start.equals(curr)) {
            Vertex next = path.get(start);
            ust.add(start);
            map.clearWall(start, next);
            start = next;
        }
    }
}

private void huntAndKill() {
    var vertices = getVertices();
    Collections.shuffle(vertices);
    HashSet<Vertex> visited = new HashSet<>();
    Vertex curr = vertices.remove(r.nextInt(vertices.size()));
    visited.add(curr);
    int n = width * height;
    while (visited.size() < n) {
        var neighbors = curr.getNeighbors(v -> inBounds(v) && !visited.contains(v));
        if (!neighbors.isEmpty()) {
            var neighbor = neighbors.get(r.nextInt(neighbors.size()));
            map.clearWall(curr, neighbor);
            curr = neighbor;
        } else {
            for (Vertex vtx : vertices) {
                neighbors = vtx.getNeighbors(v -> inBounds(v) && visited.contains(v));
                if (!neighbors.isEmpty()) {
                    var neighbor = neighbors.get(r.nextInt(neighbors.size()));
                    curr = vtx;
                    map.clearWall(curr, neighbor);
                    break;
                }
            }
        }
        visited.add(curr);
        vertices.remove(curr);
    }
}
}
