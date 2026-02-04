package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
// import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MazerApp extends Application {
    private int width = 960;
    private int height = 540;

    private VBox createGeneratorControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        Spinner<Integer> widthSpinner = new Spinner<>(3, 100, 3);
        Spinner<Integer> heightSpinner = new Spinner<>(3, 100, 3);
        ChoiceBox<String> genAlgs = new ChoiceBox<>();
        for (Generators gen : Generators.values())
            genAlgs.getItems().add(gen.toString());
        Button genBtn = new Button("Generate");
        vBox.getChildren().addAll(new Label("Mazer"),
                new Label("Width"), widthSpinner,
                new Label("Height"), heightSpinner,
                new Label("Generation Algorithms"), genAlgs, genBtn);
        return vBox;
    }

    private VBox createSolverControls() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        ChoiceBox<String> solAlgs = new ChoiceBox<>();
        for (Solvers sol : Solvers.values())
            solAlgs.getItems().add(sol.toString());
        Button solBtn = new Button("Solve");
        vBox.getChildren().addAll(
                new Label("Solving Algorithms"), solAlgs, solBtn);
        return vBox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        Button simulateBtn = new Button("Simulate");
        VBox controlsPane = new VBox(10, createGeneratorControls(), createSolverControls(), simulateBtn);
        controlsPane.setAlignment(Pos.CENTER);
        controlsPane.setPadding(new Insets(5, 5, 5, 5));
        Canvas canvas = new Canvas(0.8 * width, height);
        int[][] map = { { 13, 13, 13 }, { 1, 4, 5 }, { 7, 3, 6 } };
        Scene mainScene = new Scene(mainPane, width, height);
        // GraphicsContext g = canvas.getGraphicsContext2D();
        RayCasterView rcv = new RayCasterView(map, 1, 1, 2, 2, width, height, () -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Congrats, you solved the maze!");
                alert.showAndWait();
                stage.setScene(mainScene);
            });
        });
        simulateBtn.setOnAction((e) -> {
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