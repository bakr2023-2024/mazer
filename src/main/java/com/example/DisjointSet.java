package com.example;

import java.util.Arrays;

public class DisjointSet {
    private final int[] set;
    private final int width, height;

    public DisjointSet(int width, int height) {
        this.width = width;
        this.height = height;
        this.set = new int[width * height];
        Arrays.fill(this.set, -1);
    }

    public int getSize(Vertex v) {
        return set[find(v.y * width + v.x)];
    }

    public int find(int idx) {
        if (set[idx] < 0)
            return idx;
        set[idx] = find(set[idx]);
        return set[idx];
    }

    public boolean union(Vertex v1, Vertex v2) {
        int i1 = v1.y * width + v1.x;
        int i2 = v2.y * width + v2.x;
        int p1 = find(i1);
        int p2 = find(i2);
        if (p1 == p2)
            return false;
        if (set[p1] < set[p2]) {
            set[p1] += set[p2];
            set[p2] = p1;
        } else {
            set[p2] += set[p1];
            set[p1] = p2;
        }
        return true;
    }
}
