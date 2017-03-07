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
import piotrrr.ga.schema.Entity;
import piotrrr.ga.schema.Tree;
import piotrrr.ga.workers.Coordinator;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class Visualiser extends Application {
  // Pixels will grow by 2 x PIXEL_SIZE_GROWTH
  private static final int PIXEL_SIZE_GROWTH = 0;
  private static final String APPLICATION_NAME = "GA World Viewer";
  private static final Color BACKGROUND_COLOR = Color.WHITE;
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
      final ArrayList<Entity> toAdd = new ArrayList<>();
      world.getAddEntityObservers().add(e -> {
        synchronized (toAdd) {
          toAdd.add(e);
        }
      });
      final ArrayList<Entity> toRemove = new ArrayList<>();
      world.getRemoveEntityObservers().add(e -> {
        synchronized (toRemove) {
          toRemove.add(e);
        }
      });

      WritableImage image = new WritableImage(world.getWidth(), world.getHeight());
      while (true) {
        try {
          ArrayList<Entity> toAddCopy;
          synchronized (toAdd) {
            toAddCopy = new ArrayList<>(toAdd);
            toAdd.clear();
          }
          ArrayList<Entity> toRemoveCopy;
          synchronized (toRemove) {
            toRemoveCopy = new ArrayList<>(toRemove);
            toRemove.clear();
          }

          image = new WritableImage(image.getPixelReader(), world.getWidth(), world.getHeight());
          PixelWriter writer = image.getPixelWriter();
          toAddCopy.forEach(entity -> {
            if (entity instanceof Tree) {
              draw(writer, entity.getPosition().getX(), entity.getPosition().getY(), Color.GREEN);
            } else if (entity instanceof Animal) {
              draw(writer, entity.getPosition().getX(), entity.getPosition().getY(), Color.RED);
            }
          });
          toRemoveCopy.forEach(
              entity -> draw(writer, entity.getPosition().getX(), entity.getPosition().getY(), BACKGROUND_COLOR));

          CountDownLatch updateDone = new CountDownLatch(1);
          WritableImage finalImage = image;
          Platform.runLater(() -> {
            pane.getChildren().clear();
            ImageView view = new ImageView(finalImage);
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

  private void draw(PixelWriter writer, int x, int y, Color color) {
    for (int dx = -PIXEL_SIZE_GROWTH; dx <= PIXEL_SIZE_GROWTH; dx++) {
      for (int dy = -PIXEL_SIZE_GROWTH; dy <= PIXEL_SIZE_GROWTH; dy++) {
        int targetX = x + dx;
        int targetY = y + dy;
        if (targetX >= 0 && targetX < world.getWidth() && targetY >= 0 && targetY < world.getHeight()) {
          writer.setColor(targetX, targetY, color);
        }
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }


}