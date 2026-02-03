package com.example;

import java.util.List;
import java.util.Random;

public class Utils {
    public static final int[][] DIRS = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
    public static Random r = new Random();

    public static Vertex getRandomVtx(int width, int height) {
        return new Vertex(r.nextInt(width), r.nextInt(height));
    }

    public static Vertex getRandomVtx(List<Vertex> els){
        if(els.isEmpty())return null;
        return els.get(r.nextInt(els.size()));
    }
}
