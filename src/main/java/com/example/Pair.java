package com.example;

public class Pair {
    private Vertex next;
    private int distance;

    public Pair(Vertex next, int distance) {
        this.next = next;
        this.distance = distance;
    }

    public Vertex getNext() {
        return next;
    }

    public int getDistance() {
        return distance;
    }

    public void setNext(Vertex next) {
        this.next = next;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
