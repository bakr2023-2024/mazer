import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.DisjointSet;
import com.example.Vertex;

public class DisjointSetTest {
    private DisjointSet ds;
    private int width, height;

    @BeforeEach
    public void init() {
        width = 3;
        height = 3;
        ds = new DisjointSet(width, height);
    }

    @Test
    public void disjointSetWorks() {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(1, 0);
        Vertex v3 = new Vertex(1, 1);
        ds.union(v2, v1);
        ds.union(v2, v3);
        assertEquals(ds.getSize(v1), -3);
    }
}
