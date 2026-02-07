package com.example;

import java.util.List;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MazerApp extends Application {
    private final int MAX_N = 500;
    private int width = 1280;
    private int height = 720;
    private double cellSize;
    private MazeGenerator gen = null;
    private MazeSolver solver = new MazeSolver();
    private GraphicsContext g;
    private List<Vertex> path = null;
     private       Spinner<Integer> startXSpinner;
       private Spinner<Integer> startYSpinner;
       private Spinner<Integer> endXSpinner;
      private  Spinner<Integer> endYSpinner;
      private void setSpinners(int maxX,int maxY){
        startXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxX, 0));
        startYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxY, 0));
        endXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxX, maxX));
        endYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,maxY, maxY));
      }
    private void renderMaze() {
        if (gen == null)
            return;
        int[][] maze = gen.getMap().getMap();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, gen.getWidth() * cellSize, gen.getHeight() * cellSize);
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

    }

    private void renderSolution(List<Vertex> newPath) {
        if (path != null) {
            g.setFill(Color.BLACK);
            path.forEach(v -> g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1));
        }
        g.setFill(Color.GREEN);
        newPath.forEach(v -> g.fillRect(v.x * cellSize + 1, v.y * cellSize + 1, cellSize - 1, cellSize - 1));
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
        genBtn.setOnAction(e -> {
            int mapWidth = widthSpinner.getValue();
            int mapHeight = heightSpinner.getValue();
            setSpinners(mapWidth-1, mapHeight-1);
            Generators alg = Generators.valueOf(genAlgs.getValue());
            gen = new MazeGenerator(mapWidth, mapHeight, alg);
            cellSize = Math.min(g.getCanvas().getWidth() / mapWidth,
                            g.getCanvas().getHeight() / mapHeight);
            renderMaze();
        });
        vBox.getChildren().addAll(
                new Label("Width"), widthSpinner,
                new Label("Height"), heightSpinner,
                new Label("Generation Algorithms"), genAlgs, genBtn);
        return vBox;
    }

    private VBox createSolverControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        startXSpinner = new Spinner<>(0,MAX_N-1,0);
        startYSpinner = new Spinner<>(0,MAX_N-1,0);
        endXSpinner = new Spinner<>(0,MAX_N-1,MAX_N-1);
        endYSpinner = new Spinner<>(0,MAX_N-1,MAX_N-1);
        startXSpinner.setEditable(true);
        startYSpinner.setEditable(true);
        endXSpinner.setEditable(true);
        endYSpinner.setEditable(true);
        ChoiceBox<String> solAlgs = new ChoiceBox<>();
        for (Solvers sol : Solvers.values())
            solAlgs.getItems().add(sol.toString());
        solAlgs.setValue(Solvers.DFS.toString());
        Button solBtn = new Button("Solve");
        solBtn.setOnAction(e -> {
            if(gen==null)return;
            Vertex start = new Vertex(startXSpinner.getValue(), startYSpinner.getValue());
            Vertex end =  new Vertex(endXSpinner.getValue(), endYSpinner.getValue());
            Solvers alg = Solvers.valueOf(solAlgs.getValue());
            List<Vertex> solPath = solver.solve(gen.getMap(),start,end, alg);
            renderSolution(solPath);
            path = solPath;
        });
        vBox.getChildren().addAll(
                new Label("Start X"), startXSpinner,
                                        new Label("Start Y"), startYSpinner,
                new Label("End X"), endXSpinner,
                new Label("End Y"), endYSpinner,
                new Label("Solving Algorithms"), solAlgs, solBtn);
        return vBox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        Button simulateBtn = new Button("Simulate");
        VBox controlsPane = new VBox(10, new Label("Mazer"), createGeneratorControls(), createSolverControls(),
                simulateBtn);
        controlsPane.setAlignment(Pos.CENTER);
        controlsPane.setPadding(new Insets(5, 5, 5, 5));
        Canvas canvas = new Canvas(0.6 * width, height);
        Scene mainScene = new Scene(mainPane, width, height);
        g = canvas.getGraphicsContext2D();
        simulateBtn.setOnAction((e) -> {
            if (gen == null)
                return;
            RayCasterView rcv = new RayCasterView(gen.getMap().getMap(), 0, 0, gen.getWidth() - 1, gen.getHeight() - 1, width, height,
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
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}