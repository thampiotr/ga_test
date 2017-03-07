package piotrrr.ga;

import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.ArrayList;
import java.util.Random;

public class Coordinator {
  private World world;
  private final Random random = new Random();

  private static final long INITIAL_POPULATION_SIZE = 20;

  public Coordinator(World world) {
    this.world = world;
  }

  public void initializeWorld() {
    ArrayList<Tree> trees = createInitialTreesPopulation();
    trees.forEach(world::addEntity);
    TreesWorker treesWorker = new TreesWorker(world);
    Util.startDaemonThread(treesWorker, "Trees Worker");
    treesWorker.delegateTrees(trees);
  }

  private ArrayList<Tree> createInitialTreesPopulation() {
    ArrayList<Tree> managedTrees = new ArrayList<>();
    for (int i = 0; i < INITIAL_POPULATION_SIZE; i++) {
      long center = world.getWidth() / 2;
      long stDev = world.getWidth() / 5;
      managedTrees.add(
          newRandomTree(center, center, stDev)
      );
    }
    return managedTrees;
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
