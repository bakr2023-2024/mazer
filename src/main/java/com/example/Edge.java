package com.example;

public class Edge {
    private final Vertex v1, v2;

    public Edge(Vertex v1, Vertex v2) {
        if (v1.x != v2.x) {
            if (v1.x < v2.x) {
                this.v1 = v1;
                this.v2 = v2;
            } else {
                this.v1 = v2;
                this.v2 = v1;
            }
        } else {
            if (v1.y < v2.y) {
                this.v1 = v1;
                this.v2 = v2;
            } else {
                this.v1 = v2;
                this.v2 = v1;
            }
        }
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    @Override
    public int hashCode() {
        return 31 * v1.hashCode() + v2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Edge e))
            return false;
        return v1.equals(e.v1) && v2.equals(e.v2);
    }

    @Override
    public String toString() {
        return "[" + v1.toString() + "<=>" + v2.toString() + "]";
    }

}
