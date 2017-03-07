package piotrrr.ga;

import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreesWorker implements Runnable {
  private static final double GROWTH_RATE = 0.001;
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
      for (Tree tree : managedTrees) {
        double timeSinceLastReproduced = world.getCurrentTimeInTicks() - tree.getLastReproduced();
        // Exponential distribution
        double probabilityOfReproducing = 1.0 - Math.exp(-GROWTH_RATE * timeSinceLastReproduced);
        double chance = r.nextDouble();
        if (chance < probabilityOfReproducing) {
          tree.setLastReproduced(world.getCurrentTimeInTicks());
          Tree newTree = newRandomTree(tree.getPosition().getX(), tree.getPosition().getY(), ST_DEV_GROWN_POSITION);
          if (thereIsNoTreeThereAlready(newTree)) {
            treesToAdd.add(newTree);
          }
        }
      }
      System.out.println("treesToAdd.size() = " + treesToAdd.size());
      treesToAdd.forEach(world::addEntity);
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
        .lastReproduced(world.getCurrentTimeInTicks())
        .position(
            Position.builder()
                .x(Util.randomNormal(random, meanX, stDev, 0, world.getWidth()))
                .y(Util.randomNormal(random, meanY, stDev, 0, world.getHeight()))
                .z(0)
                .build())
        .build();
  }
}
