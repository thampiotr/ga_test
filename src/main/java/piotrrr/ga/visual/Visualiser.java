package piotrrr.ga.visual;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import piotrrr.ga.Coordinator;
import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Entity;
import piotrrr.ga.schema.Tree;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class Visualiser extends Application {

  private static final String APPLICATION_NAME = "GA World Viewer";

  @Override
  public void start(Stage stage) {
    World world = createWorld();
    visualize(stage, world);
  }

  private World createWorld() {
    World world = new World();
    Coordinator coordinator = new Coordinator(world);
    coordinator.initializeWorld();
    return world;
  }

  private void visualize(Stage stage, final World world) {
    stage.setTitle(APPLICATION_NAME);
    stage.show();

    WritableImage image = new WritableImage(world.getWidth(), world.getHeight());
    ImageView imageView = new ImageView(image);
    imageView.setSmooth(true);

    Util.startDaemonThread(() -> {
      long interval = 100;
      final ArrayList<Entity> toAdd = new ArrayList<>();
      world.getAddEntityObservers().add(e -> {
        synchronized (toAdd) {
          toAdd.add(e);
        }
      });

      while (true) {
        try {
          ArrayList<Entity> toDisplay;
          synchronized (toAdd) {
            toDisplay = new ArrayList<>(toAdd);
            toAdd.clear();
          }
          CountDownLatch updateDone = new CountDownLatch(1);
          Platform.runLater(() -> {
            System.out.println("Visualising: " + toDisplay.size());

            PixelWriter writer = image.getPixelWriter();
            toDisplay.forEach(entity -> {
              if (entity instanceof Tree) {
                writer.setColor(entity.getPosition().getX(), entity.getPosition().getY(), Color.RED);
              }
            });

            Pane pane = new Pane(imageView);
            Scene scene = new Scene(pane, world.getWidth(), world.getHeight());
            stage.setScene(scene);

            updateDone.countDown();
            System.out.println("Done Visualising.");
          });
          updateDone.await();
          Thread.sleep(interval);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, "Visualiser");

  }

  public static void main(String[] args) {
    launch(args);
  }


}