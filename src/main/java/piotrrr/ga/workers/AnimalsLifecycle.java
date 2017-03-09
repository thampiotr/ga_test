package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Genome;
import piotrrr.ga.schema.Orientation;
import piotrrr.ga.schema.Position;

import java.util.*;

public class AnimalsLifecycle implements Runnable {
  private static final double FRACTION_OF_POPULATION_TO_SELECT = 0.4;
  private static final long SELECTION_INTERVAL = 1000;
  private static final double ST_DEV_OF_NEW_ANIMAL_POSITION = 1;
  private static final double MUTATION_PROBABILITY = 0.005;
  private static final double CROSSOVER_PROBABILITY = 0.3;
  private static final int MAX_POPULATION_SIZE = 20;

  private World world;
  private final Random random = new Random();
  private long workerTime = 0L;
  private HashSet<Animal> managedAnimals = new HashSet<>();

  private double getScore(Animal animal) {
    long age = workerTime - animal.getBornTime();
    double treesEaten = animal.getTreesEaten();
    return treesEaten / age;
  }

  public AnimalsLifecycle(World world) {
    this.world = world;
    ArrayList<Animal> animals = createInitialAnimalsPopulation(MAX_POPULATION_SIZE);
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

      ArrayList<Animal> rankedAnimals = new ArrayList<>(managedAnimals);
      rankedAnimals.sort((a, b) -> -Double.compare(getScore(a), getScore(b)));
      double highScore = getScore(rankedAnimals.get(0));
      double average =
          rankedAnimals.stream().map(this::getScore).reduce(0.0, (sum, score) -> sum += score) / rankedAnimals.size();
      rankedAnimals.addAll(managedAnimals);

      int survivingRank = (int) (managedAnimals.size() * FRACTION_OF_POPULATION_TO_SELECT);

      // Deaths.
      for (int i = survivingRank; i < managedAnimals.size(); ++i) {
        double probabilityOfDeath = (double) i / MAX_POPULATION_SIZE;
        double chance = random.nextDouble();
        if (chance < probabilityOfDeath) {
          Animal animal = rankedAnimals.get(i);
          animalsToRemove.add(animal);
        }
      }

      // Births.
      for (int i = 0; i < survivingRank; ++i) {
        Animal animal = rankedAnimals.get(i);
        double probabilityOfReproduction = 1 - (double) i / MAX_POPULATION_SIZE;
        double chance = random.nextDouble();
        if (chance < probabilityOfReproduction) {
          Animal newAnimal = mutatedCopyOf(animal);
          animalsToAdd.add(newAnimal);
        }
        chance = random.nextDouble();
        if (chance < CROSSOVER_PROBABILITY) {
          Animal newAnimal = crossover(animal, rankedAnimals.get(i + 1));
          animalsToAdd.add(newAnimal);
        }
        // Reset
        animal.setTreesEaten(0);
      }

      System.out.println("animalsToAdd = " + animalsToAdd.size());
      animalsToRemove.forEach(world::removeEntity);
      managedAnimals.removeAll(animalsToRemove);
      animalsToAdd.forEach(world::addEntity);
      managedAnimals.addAll(animalsToAdd);
      System.out
          .printf(" -------- HIGH SCORE: %f. AVG: %f. POPULATION: %d -----------\n", highScore, average,
              managedAnimals.size());
      try {
        Thread.sleep((world.getTimeTick() + 1) * SELECTION_INTERVAL);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Animal newRandomAnimal(double meanX, double meanY, double stDev) {
    return Animal.builder()
        .bornTime(workerTime)
        .genome(Genome.builder()
            .data(Genome.randomBytes(random))
            .build()
        )
        .state((byte) random.nextInt(Genome.GENOME_STATE_SIZE))
        .orientation(Orientation.getRandom())
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }

  private Animal crossover(Animal a, Animal b) {
    byte[] newGenome = Arrays.copyOf(a.getGenome().getData(), a.getGenome().getData().length);
    for (int i = 0; i < newGenome.length; i += 2) {
      newGenome[i] = b.getGenome().getData()[i];
    }
    return newAnimalWithGenome(a,
        Genome.builder()
            .data(newGenome)
            .build());
  }

  private Animal mutatedCopyOf(Animal animal) {
    Genome genome = mutate(animal.getGenome());
    return newAnimalWithGenome(animal, genome);
  }

  private Genome mutate(Genome genome) {
    byte[] tmp = new byte[1];
    byte[] mutatedBytes = Arrays.copyOf(genome.getData(), genome.getData().length);
    for (int i = 0; i < mutatedBytes.length; i++) {
      double chance = random.nextDouble();
      if (chance < MUTATION_PROBABILITY) {
        random.nextBytes(tmp);
        mutatedBytes[i] = tmp[0];
      }
    }
    return Genome.builder()
        .data(mutatedBytes)
        .build();
  }

  private Animal newAnimalWithGenome(Animal animal, Genome genome) {
    return Animal.builder()
        .bornTime(workerTime)
        .genome(genome)
        .orientation(Orientation.getRandom())
        .position(
            Position.builder()
                .x(Util.randomNormal(random, animal.getPosition().getX(),
                    ST_DEV_OF_NEW_ANIMAL_POSITION, 0, world.getWidth()))
                .y(Util.randomNormal(random, animal.getPosition().getY(),
                    ST_DEV_OF_NEW_ANIMAL_POSITION, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }
}
