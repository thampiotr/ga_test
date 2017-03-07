package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AnimalsLifecycle implements Runnable {
  private static final double BIRTH_RATE = 0.001;
  private static final double DEATH_RATE = BIRTH_RATE * 0.9;
  private static final int ST_DEV_BIRTH_POSITION = 1;
  private World world;
  private final Random random = new Random();
  private long workerTime = 0L;
  private ArrayList<Animal> managedAnimals = new ArrayList<>();

  public AnimalsLifecycle(World world, int populationSize) {
    this.world = world;
    ArrayList<Animal> animals = createInitialAnimalsPopulation(populationSize);
    animals.forEach(world::addEntity);
    managedAnimals.addAll(animals);
  }

  private ArrayList<Animal> createInitialAnimalsPopulation(int populationSize) {
    ArrayList<Animal> managedAnimals = new ArrayList<>();
    for (int i = 0; i < populationSize; i++) {
      long center = world.getWidth() / 2;
      long stDev = world.getWidth() / 5;
      managedAnimals.add(
          newRandomAnimal(center, center, stDev)
      );
    }
    return managedAnimals;
  }

  @Override
  public void run() {
    while (true) {
      ++workerTime;
      List<Animal> animalsToAdd = new LinkedList<>();
      List<Animal> animalsToRemove = new LinkedList<>();
      for (Animal animal : managedAnimals) {
        double age = workerTime - animal.getBornTime();

        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-BIRTH_RATE * age);
        double chance = random.nextDouble();
        if (chance < probabilityOfReproducing) {
          Animal newAnimal = newRandomAnimal(
              animal.getPosition().getX(), animal.getPosition().getY(), ST_DEV_BIRTH_POSITION
          );
          animalsToAdd.add(newAnimal);
        }

        double probabilityOfDeath = 1.0 - Math.exp(-DEATH_RATE * age);
        chance = random.nextDouble();
        if (chance < probabilityOfDeath) {
          animalsToRemove.add(animal);
        }
      }
      animalsToRemove.forEach(world::removeEntity);
      managedAnimals.removeAll(animalsToRemove);
      animalsToAdd.forEach(world::addEntity);
      managedAnimals.addAll(animalsToAdd);
      try {
        Thread.sleep(world.getTimeTick());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // TODO: very hacky way to run the animal movement worker:
      new AnimalsMovement(world, managedAnimals).runOnce();
    }
  }

  private Animal newRandomAnimal(long meanX, double meanY, long stDev) {
    return Animal.builder()
        .bornTime(workerTime)
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }

}
