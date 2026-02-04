import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.example.BitMaze;
import com.example.Generators;
import com.example.MazeGenerator;
import com.example.Utils;
import com.example.Vertex;

public class MazeGeneratorTest {
    private MazeGenerator gen;
    private int width, height;

    public int countEdges() {
        BitMaze map = gen.getMap();
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Vertex curr = new Vertex(j, i);
                if (j < width - 1 && !map.hasWall(curr, curr.add(1, 0)))
                    count++;
                if (i < height - 1 && !map.hasWall(curr, curr.add(0, 1)))
                    count++;
            }
        }
        return count;
    }

    public boolean dfs() {
        BitMaze bm = gen.getMap();
        Vertex start = new Vertex(0, 0);
        Vertex end = new Vertex(width - 1, height - 1);
        Stack<Vertex> stack = new Stack<>();
        HashSet<Vertex> visited = new HashSet<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Vertex curr = stack.peek();
            if (curr.equals(end))
                return true;
            var neighbor = Utils.getRandomVtx(
                    curr.getNeighbors(v -> gen.inBounds(v) && !visited.contains(v) && !bm.hasWall(curr, v)));
            if (neighbor != null)
                stack.add(neighbor);
            else
                stack.pop();
        }
        return false;
    }

    @BeforeEach
    public void init() {
        width = 3;
        height = 3;
    }

    @ParameterizedTest
    @EnumSource(Generators.class)
    void testMethodWithEachEnumValue(Generators alg) {
        System.out.println(alg.toString());
        gen = new MazeGenerator(width, height, alg);
        gen.printMap();
        assertEquals(countEdges(), width * height - 1);
        assertTrue(dfs());
    }
}
