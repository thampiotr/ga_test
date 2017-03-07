package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;

public class Coordinator {
  private World world;

  private static final int INITIAL_TREES_POPULATION_SIZE = 500;
  private static final int INITIAL_ANIMALS_POPULATION_SIZE = 50;

  public Coordinator(World world) {
    this.world = world;
  }

  public void initializeWorld() {
    TreesLifecycle treesLifecycle = new TreesLifecycle(world, INITIAL_TREES_POPULATION_SIZE);
    Util.startDaemonThread(treesLifecycle, "Trees Worker");

    AnimalsLifecycle animalsLifecycle = new AnimalsLifecycle(world, INITIAL_ANIMALS_POPULATION_SIZE);
    Util.startDaemonThread(animalsLifecycle, "Animals Worker");
  }
}
