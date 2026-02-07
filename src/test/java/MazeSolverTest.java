import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.example.BitMaze;
import com.example.Generators;
import com.example.MazeGenerator;
import com.example.MazeSolver;
import com.example.Solvers;
import com.example.Vertex;

public class MazeSolverTest {
    private int width = 31;
    private int height = 26;
    private Vertex start = new Vertex(0, 0);
    private Vertex end = new Vertex(width - 1, height - 1);
    private MazeSolver solver = new MazeSolver();

    @ParameterizedTest
    @EnumSource(Solvers.class)
    public void testSolver(Solvers alg) {
        System.out.println(alg.toString());
        for (int n = 0; n < 100; n++) {
            BitMaze maze = new MazeGenerator(width, height, Generators.RECURSIVE_BACKTRACKER).getMap();
            List<Vertex> path = solver.solve(maze, start, end, alg).path;
            assertNotNull(path, "Couldn't solve maze using " + alg.toString());
            assertTrue(path.get(0).equals(start), "First vertex doesn't equal start vertex");
            assertTrue(path.get(path.size() - 1).equals(end), "Last vertex doesn't equal end vertex");
            for (int i = 0; i < path.size() - 1; i++) {
                assertFalse(maze.hasWall(path.get(i), path.get(i + 1)), "A wall exists between the two vertices");
            }
    }

}
}
