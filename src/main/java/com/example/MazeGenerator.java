package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MazeGenerator {
    private BitMaze map = null;
    private int width, height;
    private Consumer<Vertex> drawCell = null;
    private Random r;
    public static boolean stop = false;

    public void start(int width, int height, Generators alg) {
        stop = false;
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

    public void start(int width, int height, Generators alg, Consumer<Vertex> drawCell) {
        this.drawCell = drawCell;
        start(width, height, alg);
    }

    private void renderCell(Vertex v) {
        try {
            drawCell.accept(v);
            Thread.sleep(MazerApp.genDelay);
        } catch (Exception e) {
        }
    }

    public void printMap() {
        map.printMaze();
    }

    public int getCell(Vertex v) {
        return map.getMap()[v.y][v.x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        while (!stack.isEmpty() && !stop) {
            curr = stack.peek();
            visited.add(curr);
            var neighbor = Utils.getRandomVtx(curr.getNeighbors(v -> map.inbounds(v) && !visited.contains(v)));
            if (neighbor != null) {
                map.clearWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }
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
            if (stop)
                return;
            if (ds.union(e.getV1(), e.getV2())) {
                map.clearWall(e.getV1(), e.getV2());
                if (drawCell != null) {
                    renderCell(e.getV1());
                    renderCell(e.getV2());
                }
            }

        }
    }

    private void prim() {
        var vertices = getVertices();
        Vertex start = vertices.remove(r.nextInt(vertices.size()));
        HashSet<Vertex> in = new HashSet<>();
        in.add(start);
        List<Vertex> frontier = new ArrayList<>(start.getNeighbors(v -> map.inbounds(v)));
        while (!frontier.isEmpty() && !stop) {
            Vertex curr = frontier.remove(r.nextInt(frontier.size()));
            var neighbors = curr.getNeighbors(v -> map.inbounds(v) && !frontier.contains(v));
            Collections.shuffle(neighbors);
            List<Vertex> inNeighbors = new ArrayList<>();
            for (Vertex n : neighbors) {
                if (in.contains(n))
                    inNeighbors.add(n);
                else
                    frontier.add(n);
            }
            if (!inNeighbors.isEmpty()) {
                Vertex neighbor = inNeighbors.get(0);
                map.clearWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }

            }
            in.add(curr);
        }
    }

    private void aldousBroder() {
        var vertices = getVertices();
        HashSet<Vertex> visited = new HashSet<>();
        Vertex curr = vertices.remove(r.nextInt(vertices.size()));
        visited.add(curr);
        int n = width * height;
        while (visited.size() < n && !stop) {
            var neighbor = Utils.getRandomVtx(curr.getNeighbors(v -> map.inbounds(v)));
            if (!visited.contains(neighbor)) {
                map.clearWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }
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
        while (ust.size() < n && !stop) {
            Vertex curr = vertices.remove(r.nextInt(vertices.size()));
            Vertex start = curr;
            while (!ust.contains(curr) && !stop) {
                var next = Utils.getRandomVtx(curr.getNeighbors(v -> map.inbounds(v)));
                path.put(curr, next);
                curr = next;
            }
            while (!start.equals(curr) && !stop) {
                Vertex next = path.get(start);
                ust.add(start);
                map.clearWall(start, next);
                if (drawCell != null) {
                    renderCell(start);
                    renderCell(next);
                }
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
        while (visited.size() < n && !stop) {
            var neighbors = curr.getNeighbors(v -> map.inbounds(v) && !visited.contains(v));
            if (!neighbors.isEmpty()) {
                var neighbor = neighbors.get(r.nextInt(neighbors.size()));
                map.clearWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }
                curr = neighbor;
            } else {
                for (Vertex vtx : vertices) {
                    if (stop)
                        return;
                    neighbors = vtx.getNeighbors(v -> map.inbounds(v) && visited.contains(v));
                    if (!neighbors.isEmpty()) {
                        var neighbor = neighbors.get(r.nextInt(neighbors.size()));
                        curr = vtx;
                        map.clearWall(curr, neighbor);
                        if (drawCell != null) {
                            renderCell(curr);
                            renderCell(neighbor);
                        }
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
        while (!set.isEmpty() && !stop) {
            int rand = r.nextInt(3);
            int choice = rand == 0 ? 0 : rand == 1 ? r.nextInt(set.size()) : set.size() - 1;
            curr = set.get(choice);
            var neighbors = curr.getNeighbors(v -> map.inbounds(v) && !visited.contains(v));
            if (!neighbors.isEmpty()) {
                Vertex neighbor = neighbors.get(r.nextInt(neighbors.size()));
                map.clearWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }

                visited.add(neighbor);
                set.add(neighbor);
            } else
                set.remove(choice);
        }
    }

    private void recursiveDivider(int sx, int sy, int ex, int ey) {
        if (ex - sx < 1 || ey - sy < 1 || stop)
            return;
        if (ex - sx > ey - sy) {
            int wall = r.nextInt(ex - sx) + sx;
            int gap = r.nextInt(ey - sy + 1) + sy;
            for (int y = sy; y <= ey && !stop; y++) {
                if (y == gap)
                    continue;
                Vertex curr = new Vertex(wall, y);
                Vertex neighbor = new Vertex(wall + 1, y);
                map.setWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }

            }
            recursiveDivider(sx, sy, wall, ey);
            recursiveDivider(wall + 1, sy, ex, ey);
        } else {
            int wall = r.nextInt(ey - sy) + sy;
            int gap = r.nextInt(ex - sx + 1) + sx;
            for (int x = sx; x <= ex && !stop; x++) {
                if (x == gap)
                    continue;
                Vertex curr = new Vertex(x, wall);
                Vertex neighbor = new Vertex(x, wall + 1);
                map.setWall(curr, neighbor);
                if (drawCell != null) {
                    renderCell(curr);
                    renderCell(neighbor);
                }

            }
            recursiveDivider(sx, sy, ex, wall);
            recursiveDivider(sx, wall + 1, ex, ey);
        }
    }

    private void eller() {
        DisjointSet ds = new DisjointSet(width, height);
        for (int y = 0; y < height && !stop; y++) {
            for (int x = 0; x < width - 1 && !stop; x++) {
                boolean cond = y < height - 1 ? r.nextBoolean() : true;
                Vertex curr = new Vertex(x, y);
                Vertex neighbor = new Vertex(x + 1, y);
                if (cond && ds.union(curr, neighbor)) {
                    map.clearWall(curr, neighbor);
                    if (drawCell != null) {
                        renderCell(curr);
                        renderCell(neighbor);
                    }

                }
            }
            if (y < height - 1) {
                final int fy = y;
                IntStream.range(0, width).mapToObj(x -> new Vertex(x, fy))
                        .collect(Collectors.groupingBy(v -> ds.find(v.y * width + v.x)))
                        .forEach((parent, members) -> {
                            int nSamples = r.nextInt(members.size()) + 1;
                            Collections.shuffle(members);
                            for (int i = 0; i < nSamples && !stop; i++) {
                                Vertex curr = members.remove(r.nextInt(members.size()));
                                Vertex neighbor = curr.add(0, 1);
                                if (ds.union(curr, neighbor)) {
                                    map.clearWall(curr, neighbor);
                                    if (drawCell != null) {
                                        renderCell(curr);
                                        renderCell(neighbor);
                                    }

                                }
                            }
                        });
            }
        }
    }

    private void binaryTree() {
        for (int y = 0; y < height && !stop; y++) {
            for (int x = 0; x < width && !stop; x++) {
                Vertex curr = new Vertex(x, y);
                Vertex neighbor = null;
                if (x < width - 1 && y < height - 1)
                    neighbor = r.nextBoolean() ? new Vertex(x + 1, y) : new Vertex(x, y + 1);
                else if (x == width - 1 && y < height - 1)
                    neighbor = new Vertex(x, y + 1);
                else if (y == height - 1 && x < width - 1)
                    neighbor = new Vertex(x + 1, y);
                if (neighbor != null) {
                    map.clearWall(curr, neighbor);
                    if (drawCell != null) {
                        renderCell(curr);
                        renderCell(neighbor);
                    }
                }

            }
        }
    }

    private void sidewinder() {
        List<Vertex> run = new ArrayList<>();
        for (int y = 0; y < height - 1 && !stop; y++) {
            for (int x = 0; x < width && !stop; x++) {
                run.add(new Vertex(x, y));
                if (x == width - 1 || r.nextBoolean()) {
                    Vertex curr = run.get(r.nextInt(run.size()));
                    Vertex neighbor = curr.add(0, 1);
                    map.clearWall(curr, neighbor);
                    if (drawCell != null) {
                        renderCell(curr);
                        renderCell(neighbor);
                    }
                    run.clear();
                } else {
                    Vertex curr = new Vertex(x, y);
                    Vertex neighbor = new Vertex(x + 1, y);
                    map.clearWall(curr, neighbor);
                    if (drawCell != null) {
                        renderCell(curr);
                        renderCell(neighbor);
                    }
                }
            }
        }
        for (int x = 0; x < width - 1 && !stop; x++) {
            Vertex curr = new Vertex(x, height - 1);
            Vertex neighbor = new Vertex(x + 1, height - 1);
            map.clearWall(curr, neighbor);
            if (drawCell != null) {
                renderCell(curr);
                renderCell(neighbor);
            }
        }
    }
}
