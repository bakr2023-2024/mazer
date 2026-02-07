package com.example;

import java.util.HashSet;
import java.util.List;

public class SolverResult {
    public final HashSet<Vertex> visited;
    public final List<Vertex> path;

    public SolverResult(HashSet<Vertex> visited, List<Vertex> path) {
        this.visited = visited;
        this.path = path;
    }

}
