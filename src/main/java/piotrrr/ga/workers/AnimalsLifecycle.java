package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Orientation;
import piotrrr.ga.schema.Position;

import java.util.*;

public class AnimalsLifecycle implements Runnable {
  private static final double TARGET_POPULATION_SIZE = 500;
  private static final double BASE_GROWTH_RATE = 0.0005;
  private static final double MINIMUM_DEATH_RATE = BASE_GROWTH_RATE * 0.1;
  private static final double DEATH_TO_GROWTH_RATE_RATIO = 0.8;
  private static final double ST_DEV_OF_NEW_ANIMAL_POSITION = 1;

  private double growthRate = BASE_GROWTH_RATE;
  private double deathRate = growthRate * DEATH_TO_GROWTH_RATE_RATIO;
  private World world;
  private final Random random = new Random();
  private long workerTime = 0L;
  private HashSet<Animal> managedAnimals = new HashSet<>();

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

      // Growth rate linearly decreases as we approach target population
      growthRate = (1.0 - managedAnimals.size() / TARGET_POPULATION_SIZE) * BASE_GROWTH_RATE;
      // Death rate is never zero.
      deathRate = MINIMUM_DEATH_RATE + growthRate * DEATH_TO_GROWTH_RATE_RATIO;

      List<Animal> animalsToAdd = new LinkedList<>();
      List<Animal> animalsToRemove = new LinkedList<>();
      for (Animal animal : managedAnimals) {
        double age = workerTime - animal.getBornTime();

        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-growthRate * age);
        double chance = random.nextDouble();
        if (chance < probabilityOfReproducing) {
          Animal newAnimal = newRandomAnimal(
              animal.getPosition().getX(), animal.getPosition().getY(), ST_DEV_OF_NEW_ANIMAL_POSITION
          );
          animalsToAdd.add(newAnimal);
        }

        double probabilityOfDeath = 1.0 - Math.exp(-deathRate * age);
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
    }
  }

  private Animal newRandomAnimal(double meanX, double meanY, double stDev) {
    return Animal.builder()
        .bornTime(workerTime)
        .orientation(Orientation.getRandom())
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }

}
