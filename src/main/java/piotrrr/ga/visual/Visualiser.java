package piotrrr.ga.visual;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Tree;
import piotrrr.ga.workers.Coordinator;

import java.util.concurrent.CountDownLatch;


public class Visualiser extends Application {
  private static final String APPLICATION_NAME = "GA World Viewer";
  private World world;
  private static final long REFRESH_INTERVAL = 50;

  @Override
  public void start(Stage stage) {
    world = new World();
    Coordinator coordinator = new Coordinator(world);
    visualize(stage);
    coordinator.initializeWorld();
  }

  private void visualize(Stage stage) {
    stage.setTitle(APPLICATION_NAME);
    stage.show();

    Pane pane = new Pane();
    Scene scene = new Scene(pane, world.getWidth(), world.getHeight());
    stage.setScene(scene);

    Util.startDaemonThread(() -> {
      while (true) {
        try {
          WritableImage image = new WritableImage(world.getWidth(), world.getHeight());
          PixelWriter writer = image.getPixelWriter();
          world.forAllEntities(entity -> {
            if (entity instanceof Tree) {
              writer.setColor(entity.getPosition().getX(), entity.getPosition().getY(), Color.GREEN);
            } else if (entity instanceof Animal) {
              writer.setColor(entity.getPosition().getX(), entity.getPosition().getY(), Color.RED);
            }
          });

          CountDownLatch updateDone = new CountDownLatch(1);
          Platform.runLater(() -> {
            pane.getChildren().clear();
            ImageView view = new ImageView(image);
            view.setSmooth(false);
            view.setPreserveRatio(true);
            view.setFitHeight(pane.getHeight());
            view.setFitWidth(pane.getWidth());
            pane.getChildren().add(view);
            updateDone.countDown();
          });
          updateDone.await();

          Thread.sleep(REFRESH_INTERVAL);
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