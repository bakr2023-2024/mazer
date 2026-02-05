package com.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            case GROWING_TREE:
                growingTree();
                break;
            case RECURSIVE_DIVIDER:
                map.setMapForWallAdder();
                recursiveDivider(0, 0, width - 1, height - 1);
                break;
            case ELLER:
                eller();
                break;
            case BINARY_TREE:
                binaryTree();
                break;
            case SIDEWINDER:
                sidewinder();
                break;
        }
    }

    public void printMap() {
        map.printMaze();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    private void growingTree() {
        List<Vertex> set = new ArrayList<>();
        Vertex curr = Utils.getRandomVtx(width, height);
        set.add(curr);
        HashSet<Vertex> visited = new HashSet<>();
        visited.add(curr);
        while (!set.isEmpty()) {
            int rand = r.nextInt(3);
            int choice = rand == 0 ? 0 : rand == 1 ? r.nextInt(set.size()) : set.size() - 1;
            curr = set.get(choice);
            var neighbors = curr.getNeighbors(v -> inBounds(v) && !visited.contains(v));
            if (!neighbors.isEmpty()) {
                Vertex neighbor = neighbors.get(r.nextInt(neighbors.size()));
                map.clearWall(curr, neighbor);
                visited.add(neighbor);
                set.add(neighbor);
            } else
                set.remove(choice);
        }
    }

    private void recursiveDivider(int sx, int sy, int ex, int ey) {
        if (ex - sx < 1 || ey - sy < 1)
            return;
        if (ex - sx > ey - sy) {
            int wall = r.nextInt(ex - sx) + sx;
            int gap = r.nextInt(ey - sy + 1) + sy;
            for (int y = sy; y <= ey; y++) {
                if (y == gap)
                    continue;
                map.setWall(new Vertex(wall, y), new Vertex(wall + 1, y));
            }
            recursiveDivider(sx, sy, wall, ey);
            recursiveDivider(wall + 1, sy, ex, ey);
        } else {
            int wall = r.nextInt(ey - sy) + sy;
            int gap = r.nextInt(ex - sx + 1) + sx;
            for (int x = sx; x <= ex; x++) {
                if (x == gap)
                    continue;
                map.setWall(new Vertex(x, wall), new Vertex(x, wall + 1));
            }
            recursiveDivider(sx, sy, ex, wall);
            recursiveDivider(sx, wall + 1, ex, ey);
        }
    }

    private void eller() {
        DisjointSet ds = new DisjointSet(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                boolean cond = y < height - 1 ? r.nextBoolean() : true;
                Vertex curr = new Vertex(x, y);
                Vertex neighbor = new Vertex(x + 1, y);
                if (cond && ds.union(curr, neighbor))
                    map.clearWall(curr, neighbor);
            }
            if (y < height - 1) {
                final int fy = y;
                IntStream.range(0, width).mapToObj(x -> new Vertex(x, fy))
                        .collect(Collectors.groupingBy(v -> ds.find(v.y * width + v.x)))
                        .forEach((parent, members) -> {
                            int nSamples = r.nextInt(members.size()) + 1;
                            Collections.shuffle(members);
                            for (int i = 0; i < nSamples; i++) {
                                Vertex curr = members.remove(r.nextInt(members.size()));
                                Vertex neighbor = curr.add(0, 1);
                                if (ds.union(curr, neighbor))
                                    map.clearWall(curr, neighbor);
                            }
                        });
            }
        }
    }

    private void binaryTree() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width - 1 && y < height - 1)
                    map.clearWall(new Vertex(x, y), r.nextBoolean() ? new Vertex(x + 1, y) : new Vertex(x, y + 1));
                else if (x == width - 1 && y < height - 1)
                    map.clearWall(new Vertex(x, y), new Vertex(x, y + 1));
                else if (y == height - 1 && x < width - 1)
                    map.clearWall(new Vertex(x, y), new Vertex(x + 1, y));
            }
        }
    }

    private void sidewinder() {
        List<Vertex> run = new ArrayList<>();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                run.add(new Vertex(x, y));
                if (x == width - 1 || r.nextBoolean()) {
                    Vertex curr = run.get(r.nextInt(run.size()));
                    map.clearWall(curr, curr.add(0, 1));
                    run.clear();
                } else
                    map.clearWall(new Vertex(x, y), new Vertex(x + 1, y));
            }
        }
        for (int x = 0; x < width - 1; x++)
            map.clearWall(new Vertex(x, height - 1), new Vertex(x + 1, height - 1));
    }
}
