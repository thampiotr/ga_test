package piotrrr.ga.workers;

import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Position;

import java.util.ArrayList;
import java.util.Random;

public class AnimalsMovement implements Runnable {
  private static final int STEP_SIZE = 1;
  private World world;
  private final Random random = new Random();
  private ArrayList<Animal> managedAnimals;

  public AnimalsMovement(World world, ArrayList<Animal> managedAnimals) {
    this.world = world;
    this.managedAnimals = managedAnimals;
  }

  @Override
  public void run() {
    throw new UnsupportedOperationException("Nope.");
  }

  public void runOnce() {
    for (Animal animal : managedAnimals) {
      world.removeEntity(animal);

      animal.setPosition(Position.builder()
          .x(animal.getPosition().getX() + randomMove())
          .y(animal.getPosition().getY() + randomMove())
          .build()
      );

      world.addEntity(animal);
    }
  }

  private int randomMove() {
    return random.nextInt(2 * STEP_SIZE + 1) - STEP_SIZE;
  }

}
