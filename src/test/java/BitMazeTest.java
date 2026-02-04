import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.BitMaze;
import com.example.Vertex;

public class BitMazeTest {
    private BitMaze maze;
    private int width, height;
    private Vertex curr, neighbor;

    @BeforeEach
    public void init() {
        width = 3;
        height = 3;
        maze = new BitMaze(width, height);
        curr = new Vertex(0, 0);
        neighbor = new Vertex(1, 0);
    }

    @Test
    public void eachCellHasAllWalls() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertTrue(maze.hasWall(new Vertex(x, y), new Vertex(x - 1, y)));
                assertTrue(maze.hasWall(new Vertex(x, y), new Vertex(x + 1, y)));
                assertTrue(maze.hasWall(new Vertex(x, y), new Vertex(x, y - 1)));
                assertTrue(maze.hasWall(new Vertex(x, y), new Vertex(x, y + 1)));
            }
        }
    }

    @Test
    public void setMapForWallAdderWorks() {
        maze.setMapForWallAdder();
        int[][] map = maze.getMap();
        int[][] mask = { { 9, 8, 12 }, { 1, 0, 4 }, { 3, 2, 6 } };
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertEquals(mask[y][x], map[y][x]);
            }
        }
    }

    @Test
    public void getDirWorks() {
        Vertex right = curr.add(1, 0);
        Vertex left = curr.add(-1, 0);
        Vertex top = curr.add(0, -1);
        Vertex bottom = curr.add(0, 1);
        assertEquals(maze.getDir(curr, right), BitMaze.RIGHT);
        assertEquals(maze.getDir(curr, left), BitMaze.LEFT);
        assertEquals(maze.getDir(curr, top), BitMaze.TOP);
        assertEquals(maze.getDir(curr, bottom), BitMaze.BOTTOM);
    }

    @Test
    public void clearWallWorks() {
        maze.clearWall(curr, neighbor);
        assertFalse(maze.hasWall(curr, neighbor));
        assertFalse(maze.hasWall(neighbor, curr));
    }

    @Test
    public void setWallWorks() {
        maze.setWall(curr, neighbor);
        assertTrue(maze.hasWall(curr, neighbor));
        assertTrue(maze.hasWall(neighbor, curr));
    }

}
