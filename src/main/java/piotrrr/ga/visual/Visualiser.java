package piotrrr.ga.visual;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import piotrrr.ga.BushesWorker;
import piotrrr.ga.World;

import java.util.List;
import java.util.stream.Collectors;


public class Visualiser extends Application {

    private static final int TICK_UNIT = -1;
    private static final String APPLICATION_NAME = "GA World Viewer";

    @Override
    public void start(Stage stage) {

        World world = new World();
        startThread(new BushesWorker(world), "Bushes Worker");

        stage.setTitle(APPLICATION_NAME);
        final NumberAxis xAxis = new NumberAxis(0, world.getWidth(), TICK_UNIT);
        final NumberAxis yAxis = new NumberAxis(0, world.getHeight(), TICK_UNIT);
        final ScatterChart<Number, Number> sc = new
                ScatterChart<>(xAxis, yAxis);
        xAxis.setLabel("x");
        yAxis.setLabel("y");
        sc.setTitle(APPLICATION_NAME);

        sc.setAnimated(false);

        XYChart.Series bushesLayer = new XYChart.Series();
        bushesLayer.setName("Bushes");

        AnimationTimer bushesVisualiser = new AnimationTimer() {
            private long lastUpdate = 0;
            private long interval = 200;

            @SuppressWarnings("unchecked")
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= interval) {
                    bushesLayer.getData().clear();
                    List<XYChart.Data> newData = world.getBushes().stream()
                            .map(bush -> new XYChart.Data(bush.getPosition().getX(), bush.getPosition().getY()))
                            .collect(Collectors.toList());
                    bushesLayer.getData().addAll(newData);
                    lastUpdate = now;
                }
            }
        };
        bushesVisualiser.start();


        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Mutual funds");
        series2.getData().add(new XYChart.Data(5.2, 229.2));
        series2.getData().add(new XYChart.Data(2.4, 37.6));
        series2.getData().add(new XYChart.Data(3.2, 49.8));
        series2.getData().add(new XYChart.Data(1.8, 134));

        sc.getData().addAll(bushesLayer, series2);
        Scene scene = new Scene(sc, world.getWidth(), world.getHeight());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void startThread(Runnable r, String name) {
        Thread thread = new Thread(r, name);
        thread.setDaemon(true);
        thread.start();
    }

}