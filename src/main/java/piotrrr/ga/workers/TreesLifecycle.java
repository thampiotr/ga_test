package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.*;

public class TreesLifecycle implements Runnable {
  private static final double TARGET_POPULATION_SIZE = 50000;
  private static final double BASE_GROWTH_RATE = 0.0005;
  private static final double MINIMUM_DEATH_RATE = BASE_GROWTH_RATE * 0.01;
  private static final double DEATH_TO_GROWTH_RATE_RATIO = 0.9;
  private static final double ST_DEV_OF_NEW_TREE_POSITION = 1;

  private double growthRate = BASE_GROWTH_RATE;
  private double deathRate = growthRate * DEATH_TO_GROWTH_RATE_RATIO;
  private World world;
  private final Random random = new Random();
  private long workerTime = 0L;
  private HashSet<Tree> managedTrees = new HashSet<>();

  public TreesLifecycle(World world, int populationSize) {
    this.world = world;
    ArrayList<Tree> trees = createInitialTreesPopulation(populationSize);
    trees.forEach(world::addEntity);
  }

  private ArrayList<Tree> createInitialTreesPopulation(int populationSize) {
    ArrayList<Tree> managedTrees = new ArrayList<>();
    for (int i = 0; i < populationSize; i++) {
      long center = world.getWidth() / 2;
      long stDev = world.getWidth() / 5;
      managedTrees.add(
          newRandomTree(center, center, stDev)
      );
    }
    return managedTrees;
  }

  @Override
  public void run() {
    while (true) {
      ++workerTime;

      // managedTrees is empty at the beginning of each iteration.
      world.forAllEntities(entity -> {
        if (entity instanceof Tree) {
          managedTrees.add((Tree) entity);
        }
      });

      // Growth rate linearly decreases as we approach target population.
      growthRate = (1.0 - managedTrees.size() / TARGET_POPULATION_SIZE) * BASE_GROWTH_RATE;
      // Death rate is never zero.
      deathRate = MINIMUM_DEATH_RATE + growthRate * DEATH_TO_GROWTH_RATE_RATIO;

      List<Tree> treesToAdd = new LinkedList<>();
      List<Tree> treesToRemove = new LinkedList<>();
      for (Tree tree : managedTrees) {
        double age = workerTime - tree.getBornTime();
        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-growthRate * age);
        double chance = random.nextDouble();
        if (chance < probabilityOfReproducing) {
          int attempts = 5;
          while (attempts-- > 0) {
            Tree newTree = newRandomTree(tree.getPosition().getX(), tree.getPosition().getY(),
                ST_DEV_OF_NEW_TREE_POSITION);
            if (thereIsNoTreeThereAlready(newTree)) {
              treesToAdd.add(newTree);
              break;
            }
          }
        }

        double probabilityOfDeath = 1.0 - Math.exp(-deathRate * age);
        chance = random.nextDouble();
        if (chance < probabilityOfDeath) {
          treesToRemove.add(tree);
        }
      }
      treesToRemove.forEach(world::removeEntity);
      managedTrees.removeAll(treesToRemove);
      treesToAdd.forEach(world::addEntity);
      managedTrees.addAll(treesToAdd);

      // Clear before sleeping to free up some memory.
      managedTrees.clear();
      try {
        Thread.sleep(world.getTimeTick());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean thereIsNoTreeThereAlready(Tree newTree) {
    return world.getEntitiesAt(newTree.getPosition().getX(), newTree.getPosition().getY()).stream()
        .filter(e -> e instanceof Tree).count() == 0;
  }

  private Tree newRandomTree(double meanX, double meanY, double stDev) {
    return Tree.builder()
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
