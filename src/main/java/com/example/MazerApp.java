package com.example;

import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MazerApp extends Application {
    private final int MAX_N = 500;
    public static int genDelay = 10;
    public static int solDelay = 10;
    private int width = 1280;
    private int height = 720;
    private double cellSize;
    private MazeGenerator gen = new MazeGenerator();
    private MazeSolver solver = new MazeSolver();
    private GraphicsContext g;
    private Vertex start, end;
    private Spinner<Integer> startXSpinner = new Spinner<>(0, 2, 0);
    private Spinner<Integer> startYSpinner = new Spinner<>(0, 2, 0);
    private Spinner<Integer> endXSpinner = new Spinner<>(0, 2, 2);
    private Spinner<Integer> endYSpinner = new Spinner<>(0, 2, 2);
    private Thread genThread = null;
    private Thread solThread = null;
    private Consumer<Vertex> drawGenCell = (v) -> {
        Platform.runLater(() -> {
            int cell = gen.getCell(v);
            g.setFill(Color.BLACK);
            g.fillRect(v.x * cellSize, v.y * cellSize, cellSize, cellSize);
            g.setStroke(Color.WHITE);
            if ((cell & 1 << BitMaze.TOP) != 0)
                g.strokeLine(v.x * cellSize, v.y * cellSize, (v.x + 1) * cellSize, v.y * cellSize);
            if ((cell & 1 << BitMaze.RIGHT) != 0)
                g.strokeLine((v.x + 1) * cellSize, v.y * cellSize, (v.x + 1) * cellSize, (v.y + 1) * cellSize);
            if ((cell & 1 << BitMaze.BOTTOM) != 0)
                g.strokeLine(v.x * cellSize, (v.y + 1) * cellSize, (v.x + 1) * cellSize, (v.y + 1) * cellSize);
            if ((cell & 1 << BitMaze.LEFT) != 0)
                g.strokeLine(v.x * cellSize, v.y * cellSize, v.x * cellSize, (v.y + 1) * cellSize);
        });
    };
    private Consumer<Vertex> drawSolCell = (v) -> {
        Platform.runLater(() -> {
            g.setFill(Color.RED);
            g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
        try {
            Thread.sleep(solDelay);
        } catch (Exception e) {
        }
        Platform.runLater(() -> {
            g.setFill(Color.YELLOW);
            g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
    };
    private void setSpinners(int maxX, int maxY) {
        startXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxX, 0));
        startYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxY, 0));
        endXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxX, maxX));
        endYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxY, maxY));
      }

      private void clearMaze() {
          g.setFill(Color.BLACK);
          g.fillRect(0, 0, g.getCanvas().getWidth(),g.getCanvas().getHeight());
      }

      private void renderMaze() {
          if (gen.getMap() == null)
            return;
        int[][] maze = gen.getMap().getMap();
        clearMaze();
        g.setStroke(Color.WHITE);
        for (int y = 0; y < gen.getHeight(); y++) {
            for (int x = 0; x < gen.getWidth(); x++) {
                if ((maze[y][x] & 1 << BitMaze.TOP) != 0)
                    g.strokeLine(x * cellSize, y * cellSize, (x + 1) * cellSize, y * cellSize);
                if ((maze[y][x] & 1 << BitMaze.RIGHT) != 0)
                    g.strokeLine((x + 1) * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize);
                if ((maze[y][x] & 1 << BitMaze.BOTTOM) != 0)
                    g.strokeLine(x * cellSize, (y + 1) * cellSize, (x + 1) * cellSize, (y + 1) * cellSize);
                if ((maze[y][x] & 1 << BitMaze.LEFT) != 0)
                    g.strokeLine(x * cellSize, y * cellSize, x * cellSize, (y + 1) * cellSize);
            }
        }
        g.setFill(Color.BLUE);
        g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
        g.setFill(Color.FUCHSIA);
        g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
    }

    private void clearSolution() {
        renderMaze();
    }

    private void renderSolution(SolverResult solution, boolean withVisited) {
        if (solution == null)
            return;
        clearSolution();
        if (withVisited) {
        g.setFill(Color.YELLOW);
        solution.visited.forEach(v -> g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1));
    }
        g.setFill(Color.GREEN);
        solution.path.forEach(v -> g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1));
        g.setFill(Color.BLUE);
        g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
        g.setFill(Color.FUCHSIA);
        g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
    }

    private VBox createGeneratorControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        Spinner<Integer> widthSpinner = new Spinner<>(3, MAX_N, 3);
        widthSpinner.setEditable(true);
        Spinner<Integer> heightSpinner = new Spinner<>(3, MAX_N, 3);
        heightSpinner.setEditable(true);
        ChoiceBox<String> genAlgs = new ChoiceBox<>();
        for (Generators gen : Generators.values())
            genAlgs.getItems().add(gen.toString());
        genAlgs.setValue(Generators.RECURSIVE_BACKTRACKER.toString());
        Button genBtn = new Button("Generate");
        Button animateBtn = new Button("Animate");
        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(e -> {
            if (genThread != null) {
                MazeGenerator.stop = true;
                genThread.interrupt();
            }
        });
        animateBtn.setOnAction(e -> {
            if (genThread != null) {
                MazeGenerator.stop = true;
                genThread.interrupt();
            }
            int mapWidth = widthSpinner.getValue();
            int mapHeight = heightSpinner.getValue();
            Generators alg = Generators.valueOf(genAlgs.getValue());
            setSpinners(mapWidth - 1, mapHeight - 1);
            start = new Vertex(0, 0);
            end = new Vertex(mapWidth - 1, mapHeight - 1);
            cellSize = Math.min(g.getCanvas().getWidth() / mapWidth, g.getCanvas().getHeight() / mapHeight);
            Platform.runLater(this::clearMaze);
            genThread = new Thread(() -> gen.start(mapWidth, mapHeight, alg, drawGenCell));
            genThread.start();
        });
        genBtn.setOnAction(e -> {
            if (genThread != null) {
                MazeGenerator.stop = true;
                genThread.interrupt();
            }
            int mapWidth = widthSpinner.getValue();
            int mapHeight = heightSpinner.getValue();
            Generators alg = Generators.valueOf(genAlgs.getValue());
            setSpinners(mapWidth-1, mapHeight-1);
            start = new Vertex(0, 0);
            end = new Vertex(mapWidth - 1, mapHeight - 1);
            cellSize = Math.min(g.getCanvas().getWidth() / mapWidth,
                    g.getCanvas().getHeight() / mapHeight);
            Platform.runLater(this::clearMaze);
            genThread = new Thread(() -> {
                gen.start(mapWidth, mapHeight, alg, null);
                Platform.runLater(this::renderMaze);
            });
            genThread.start();
        });
        vBox.getChildren().addAll(
                new Label("Width"), widthSpinner,
                new Label("Height"), heightSpinner,
                new Label("Generation Algorithms"), genAlgs, genBtn, animateBtn, stopBtn);
        genBtn.fire();
        return vBox;
    }

    private VBox createSolverControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        startXSpinner.valueProperty().addListener((e, oldV, newV) -> {
            g.setFill(Color.BLACK);
            g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
            start.x = newV;
            g.setFill(Color.BLUE);
            g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
        startYSpinner.valueProperty().addListener((e, oldV, newV) -> {
            g.setFill(Color.BLACK);
            g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
            start.y = newV;
            g.setFill(Color.BLUE);
            g.fillRect(start.x * cellSize + 1, start.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
        endXSpinner.valueProperty().addListener((e, oldV, newV) -> {
            g.setFill(Color.BLACK);
            g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
            end.x = newV;
            g.setFill(Color.FUCHSIA);
            g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
        endYSpinner.valueProperty().addListener((e, oldV, newV) -> {
            g.setFill(Color.BLACK);
            g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
            end.y = newV;
            g.setFill(Color.FUCHSIA);
            g.fillRect(end.x * cellSize + 1, end.y * cellSize + 1, cellSize - 1, cellSize - 1);
        });
        startXSpinner.setEditable(true);
        startYSpinner.setEditable(true);
        endXSpinner.setEditable(true);
        endYSpinner.setEditable(true);
        ChoiceBox<String> solAlgs = new ChoiceBox<>();
        for (Solvers sol : Solvers.values())
            solAlgs.getItems().add(sol.toString());
        solAlgs.setValue(Solvers.DFS.toString());
        Button solBtn = new Button("Solve");
        Button animateBtn = new Button("Animate");
        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(e -> {
            if (solThread != null) {
                MazeSolver.stop = true;
                solThread.interrupt();
            }
        });
        animateBtn.setOnAction(e -> {
            if (solThread != null) {
                MazeSolver.stop = true;
                solThread.interrupt();
            }
            if (gen == null)
                return;
            start = new Vertex(startXSpinner.getValue(), startYSpinner.getValue());
            end = new Vertex(endXSpinner.getValue(), endYSpinner.getValue());
            Solvers alg = Solvers.valueOf(solAlgs.getValue());
            Platform.runLater(this::clearSolution);
            solThread = new Thread(() -> {
                SolverResult solResult = solver.solve(gen.getMap(), start, end, alg, drawSolCell);
                Platform.runLater(() -> renderSolution(solResult, false));
            });
            solThread.start();
        });
        solBtn.setOnAction(e -> {
            if (solThread != null) {
                MazeSolver.stop = true;
                solThread.interrupt();
            }
            if(gen==null)return;
            start = new Vertex(startXSpinner.getValue(), startYSpinner.getValue());
            end = new Vertex(endXSpinner.getValue(), endYSpinner.getValue());
            Solvers alg = Solvers.valueOf(solAlgs.getValue());
            Platform.runLater(this::clearSolution);
            solThread = new Thread(() -> {
                SolverResult solResult = solver.solve(gen.getMap(), start, end, alg, null);
                Platform.runLater(() -> renderSolution(solResult, true));
            });
            solThread.start();
        });
        vBox.getChildren().addAll(
                new Label("Start X"), startXSpinner,
                                        new Label("Start Y"), startYSpinner,
                new Label("End X"), endXSpinner,
                new Label("End Y"), endYSpinner,
                new Label("Solving Algorithms"), solAlgs, solBtn, animateBtn, stopBtn);
        return vBox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        Button simulateBtn = new Button("Simulate");

        Canvas canvas = new Canvas(0.6 * width, height);
        Scene mainScene = new Scene(mainPane, width, height);
        g = canvas.getGraphicsContext2D();
        VBox controlsPane = new VBox(10, new Label("Mazer"), createGeneratorControls(), createSolverControls(),
                simulateBtn);
        controlsPane.setAlignment(Pos.CENTER);
        controlsPane.setPadding(new Insets(5, 5, 5, 5));
        simulateBtn.setOnAction((e) -> {
            if (gen == null)
                return;
            RayCasterView rcv = new RayCasterView(gen.getMap().getMap(), start.x, start.y, end.x, end.y, width, height,
                    () -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Congrats, you solved the maze!");
                alert.showAndWait();
                stage.setScene(mainScene);
            });
            });
            stage.setScene(rcv.getScene());
            rcv.start();
        });
        mainPane.setRight(controlsPane);
        mainPane.setCenter(canvas);
        mainScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(mainScene);
        stage.setOnCloseRequest(e -> {
            MazeGenerator.stop = true;
            MazeSolver.stop = true;
            if (genThread != null)
                genThread.interrupt();
            if (solThread != null)
                solThread.interrupt();
            Platform.exit();
        });
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/favicon.png")));
        stage.setTitle("Mazer");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}