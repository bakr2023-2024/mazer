
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.Vertex;

public class VertexTest {
    private static Vertex vtx;
    private static int x, y;

    @BeforeAll
    public static void init() {
        x = 3;
        y = 2;
        vtx = new Vertex(x, y);
    }

    @Test
    public void correctVertexCoordinates() {
        Vertex vertex = new Vertex(x, y);
        assertEquals(vertex.x, x);
        assertEquals(vertex.y, y);
    }

    @Test
    public void correctVertexEquality() {
        assertTrue(vtx.equals(new Vertex(x, y)));
    }

    @Test
    public void correctVertexAddition() {
        Vertex oth = new Vertex(2, 1);
        Vertex result = vtx.add(oth);
        assertEquals(result.x, x + 2);
        assertEquals(result.y, y + 1);
    }

    @Test
    public void correctFilteredNeighbors() {
        Vertex out = new Vertex(x - 1, y);
        List<Vertex> neighbors = vtx.getNeighbors(v -> !v.equals(out));
        assertFalse(neighbors.contains(out));
        assertEquals(neighbors.size(), 3);
    }

}
