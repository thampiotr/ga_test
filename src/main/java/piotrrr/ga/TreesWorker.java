package piotrrr.ga;

import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreesWorker implements Runnable {
  private static final double GROWTH_RATE = 0.0001;
  private static final double DEATH_RATE = GROWTH_RATE * 0.9;
  private static final int ST_DEV_GROWN_POSITION = 10;
  private World world;
  private final Random random = new Random();
  private ArrayList<Tree> managedTrees = new ArrayList<>();

  public TreesWorker(World world) {
    this.world = world;
  }

  public void delegateTrees(ArrayList<Tree> trees) {
    managedTrees.addAll(trees);
  }

  @Override
  public void run() {
    while (true) {
      Random r = new Random();
      List<Tree> treesToAdd = new LinkedList<>();
      List<Tree> treesToRemove = new LinkedList<>();
      for (Tree tree : managedTrees) {
        double age = world.getCurrentTimeInTicks() - tree.getBornTime();

        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-GROWTH_RATE * age);
        double chance = r.nextDouble();
        if (chance < probabilityOfReproducing) {
          tree.setBornTime(world.getCurrentTimeInTicks());
          Tree newTree = newRandomTree(tree.getPosition().getX(), tree.getPosition().getY(), ST_DEV_GROWN_POSITION);
          if (thereIsNoTreeThereAlready(newTree)) {
            treesToAdd.add(newTree);
          }
        }

        double probabilityOfDeath = 1.0 - Math.exp(-DEATH_RATE * age);
        chance = r.nextDouble();
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
        .bornTime(world.getCurrentTimeInTicks())
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }
}
