package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreesWorker implements Runnable {
  private static final double GROWTH_RATE = 0.0001;
  private static final double DEATH_RATE = GROWTH_RATE * 0.9;
  private static final int ST_DEV_GROWN_POSITION = 5;
  private World world;
  private final Random random = new Random();
  private long workerTime = 0L;
  private ArrayList<Tree> managedTrees = new ArrayList<>();

  public TreesWorker(World world, int populationSize) {
    this.world = world;
    ArrayList<Tree> trees = createInitialTreesPopulation(populationSize);
    trees.forEach(world::addEntity);
    managedTrees.addAll(trees);
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
      List<Tree> treesToAdd = new LinkedList<>();
      List<Tree> treesToRemove = new LinkedList<>();
      for (Tree tree : managedTrees) {
        double age = workerTime - tree.getBornTime();
        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-GROWTH_RATE * age);
        double chance = random.nextDouble();
        if (chance < probabilityOfReproducing) {
          Tree newTree = newRandomTree(tree.getPosition().getX(), tree.getPosition().getY(), ST_DEV_GROWN_POSITION);
          if (thereIsNoTreeThereAlready(newTree)) {
            treesToAdd.add(newTree);
          }
        }

        double probabilityOfDeath = 1.0 - Math.exp(-DEATH_RATE * age);
        chance = random.nextDouble();
        if (chance < probabilityOfDeath) {
          treesToRemove.add(tree);
        }
      }
      treesToAdd.forEach(world::addEntity);
      treesToRemove.forEach(world::removeEntity);
      managedTrees.addAll(treesToAdd);
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

  private Tree newRandomTree(long meanX, double meanY, long stDev) {
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
