package com.example;

public class Pair {
    private Vertex prev;
    private int distance;

    public Pair(Vertex prev, int distance) {
        this.prev = prev;
        this.distance = distance;
    }

    public Vertex getPrev() {
        return prev;
    }

    public int getDistance() {
        return distance;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
