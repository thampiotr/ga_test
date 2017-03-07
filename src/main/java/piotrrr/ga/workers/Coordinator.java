package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;

public class Coordinator {
  private World world;

  private static final int INITIAL_TREES_POPULATION_SIZE = 100;
  private static final int INITIAL_ANIMALS_POPULATION_SIZE = 50;

  public Coordinator(World world) {
    this.world = world;
  }

  public void initializeWorld() {
    TreesWorker treesWorker = new TreesWorker(world, INITIAL_TREES_POPULATION_SIZE);
    Util.startDaemonThread(treesWorker, "Trees Worker");

    AnimalsWorker animalsWorker = new AnimalsWorker(world, INITIAL_ANIMALS_POPULATION_SIZE);
    Util.startDaemonThread(animalsWorker, "Animals Worker");
  }


}
