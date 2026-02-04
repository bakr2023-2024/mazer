package com.example;

public class Edge {
    private final Vertex v1, v2;

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Edge))
            return false;
        Edge oth = (Edge) obj;
        return this.v1.equals(oth.v1) && this.v2.equals(oth.v2) || this.v1.equals(oth.v2) && this.v2.equals(oth.v1);
    }

    @Override
    public String toString() {
        return "[" + v1.toString() + "<=>" + v2.toString() + "]";
    }

}
