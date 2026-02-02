package com.example;

public class Vertex {
    private int x, y;

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

}
