package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Vertex {
    public final int x, y;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return (31 * x) ^ y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Vertex))
            return false;
        Vertex other = (Vertex) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    public Vertex add(Vertex oth) {
        return new Vertex(x + oth.x, y + oth.y);
    }

    public List<Vertex> getNeighbors(Predicate<Vertex> fn) {
        List<Vertex> neighbors = new ArrayList<>();
        for (int[] dir : Utility.DIRS) {
            Vertex curr = new Vertex(x + dir[0], y + dir[1]);
            if (fn.test(curr))
                neighbors.add(curr);
        }
        return neighbors;
    }
}
