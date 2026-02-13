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
    private Spinner<Integer> widthSpinner = new Spinner<>(3, MAX_N, 3);
    private Spinner<Integer> heightSpinner = new Spinner<>(3, MAX_N, 3);
    private int mapWidth = 3;
    private int mapHeight = 3;
    private ChoiceBox<String> genAlgs;
    private ChoiceBox<String> solAlgs;
    private Thread genThread = null;
    private Thread solThread = null;
    private Consumer<Vertex> drawGenCell = (v) -> {
        Platform.runLater(() -> {
            g.setFill(Color.BLACK);
            g.fillRect(v.x * cellSize, v.y * cellSize, cellSize, cellSize);
            int cell = gen.getCell(v);
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
        Platform.runLater(() -> renderVertex(v, Color.RED));
        try {
            Thread.sleep(solDelay);
        } catch (Exception e) {
        }
        Platform.runLater(() ->

        renderVertex(v, v.equals(start) ? Color.BLUE : Color.YELLOW));
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
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
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
        renderVertex(start, Color.BLUE);
        renderVertex(end, Color.FUCHSIA);
    }

    private void renderVertex(Vertex v, Color color) {
        g.setFill(color);
        g.fillRect(v.x * cellSize + 1,
                v.y * cellSize + 1,
                cellSize - 1,
                cellSize - 1);
    }

    private void renderSolution(SolverResult solution) {
        if (solution == null)
            return;
        renderMaze();
        solution.visited.forEach(v -> renderVertex(v, Color.YELLOW));
        solution.path.forEach(v -> renderVertex(v, Color.GREEN));
        renderVertex(start, Color.BLUE);
        renderVertex(end, Color.FUCHSIA);
    }

    private void stopGenTask() {
        if (genThread != null) {
            MazeGenerator.stop = true;
            genThread.interrupt();
        }
    }

    private void startGenTask(boolean animate) {
        if (genThread != null) {
            MazeGenerator.stop = true;
            genThread.interrupt();
        }
        mapWidth = widthSpinner.getValue();
        mapHeight = heightSpinner.getValue();
        Generators alg = Generators.valueOf(genAlgs.getValue());
        setSpinners(mapWidth - 1, mapHeight - 1);
        start = new Vertex(0, 0);
        end = new Vertex(mapWidth - 1, mapHeight - 1);
        cellSize = Math.min(g.getCanvas().getWidth() / mapWidth, g.getCanvas().getHeight() / mapHeight);
        Platform.runLater(this::clearMaze);
        genThread = new Thread(() -> {
            gen.start(mapWidth, mapHeight, alg, animate ? drawGenCell : null);
            if (!animate)
                renderMaze();
        });
        genThread.start();
    }

    private VBox createGeneratorControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        widthSpinner.setEditable(true);
        heightSpinner.setEditable(true);
        genAlgs = new ChoiceBox<>();
        for (Generators gen : Generators.values())
            genAlgs.getItems().add(gen.toString());
        genAlgs.setValue(Generators.RECURSIVE_BACKTRACKER.toString());
        Button genBtn = new Button("Generate");
        Button animateBtn = new Button("Animate");
        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(e -> stopGenTask());
        animateBtn.setOnAction(e -> startGenTask(true));
        genBtn.setOnAction(e -> startGenTask(false));
        vBox.getChildren().addAll(
                new Label("Width"), widthSpinner,
                new Label("Height"), heightSpinner,
                new Label("Generation Algorithms"), genAlgs, genBtn, animateBtn, stopBtn);
        genBtn.fire();
        return vBox;
    }

    private void stopSolTask() {
        if (solThread != null) {
            MazeSolver.stop = true;
            solThread.interrupt();
        }
    }

    private void startSolTask(boolean animate) {
        start = new Vertex(startXSpinner.getValue(), startYSpinner.getValue());
        end = new Vertex(endXSpinner.getValue(), endYSpinner.getValue());
        Solvers alg = Solvers.valueOf(solAlgs.getValue());
        Platform.runLater(this::renderMaze);
        solThread = new Thread(() -> {
            SolverResult solResult = solver.solve(gen.getMap(), start, end, alg, animate ? drawSolCell : null);
            Platform.runLater(() -> renderSolution(solResult));
        });
        solThread.start();
    }
    private VBox createSolverControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        startXSpinner.valueProperty().addListener((e, oldV, newV) -> {
            renderVertex(start, Color.BLACK);
            start.x = newV;
            renderVertex(start, Color.BLUE);
        });
        startYSpinner.valueProperty().addListener((e, oldV, newV) -> {
            renderVertex(start, Color.BLACK);
            start.y = newV;
            renderVertex(start, Color.BLUE);
        });
        endXSpinner.valueProperty().addListener((e, oldV, newV) -> {
            renderVertex(end, Color.BLACK);
            end.x = newV;
            renderVertex(end, Color.FUCHSIA);
        });
        endYSpinner.valueProperty().addListener((e, oldV, newV) -> {
            renderVertex(end, Color.BLACK);
            end.y = newV;
            renderVertex(end, Color.FUCHSIA);
        });
        startXSpinner.setEditable(true);
        startYSpinner.setEditable(true);
        endXSpinner.setEditable(true);
        endYSpinner.setEditable(true);
        solAlgs = new ChoiceBox<>();
        for (Solvers sol : Solvers.values())
            solAlgs.getItems().add(sol.toString());
        solAlgs.setValue(Solvers.DFS.toString());
        Button solBtn = new Button("Solve");
        Button animateBtn = new Button("Animate");
        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(e -> stopSolTask());
        animateBtn.setOnAction(e -> startSolTask(true));
        solBtn.setOnAction(e -> startSolTask(false));
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
        launch(args);
    }
}