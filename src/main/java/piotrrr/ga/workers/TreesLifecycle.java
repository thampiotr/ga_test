package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;
import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.*;

public class TreesLifecycle implements Runnable {
  private static final int MAX_POPULATION_SIZE = 10000;
  private static final double ST_DEV_OF_NEW_TREE_POSITION = 1;
  private static final int INITIAL_TREES = 100;

  private World world;
  private final Random random = new Random();
  private HashSet<Tree> managedTrees = new HashSet<>();

  public TreesLifecycle(World world) {
    this.world = world;
    ArrayList<Tree> trees = createInitialTreesPopulation(INITIAL_TREES);
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

      // managedTrees is empty at the beginning of each iteration.
      world.forAllEntities(entity -> {
        if (entity instanceof Tree) {
          managedTrees.add((Tree) entity);
        }
      });

      List<Tree> treesToAdd = new LinkedList<>();
      double treeReproductionProbability =
          ((double) MAX_POPULATION_SIZE - managedTrees.size()) / MAX_POPULATION_SIZE;
      for (Tree tree : managedTrees) {
        double chance = random.nextDouble();
        if (chance < treeReproductionProbability) {
          int attempts = 10;
          while (attempts-- > 0) {
            Tree newTree = newRandomTree(
                tree.getPosition().getX(), tree.getPosition().getY(), ST_DEV_OF_NEW_TREE_POSITION
            );
            if (thereIsNoTreeThereAlready(newTree)) {
              treesToAdd.add(newTree);
              break;
            }
          }
        }

      }
      treesToAdd.forEach(world::addEntity);

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
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }
}
