package piotrrr.ga.workers;

import piotrrr.ga.Util;
import piotrrr.ga.World;

public class Coordinator {
  private World world;

  public Coordinator(World world) {
    this.world = world;
  }

  public void initializeWorld() {
    TreesLifecycle treesLifecycle = new TreesLifecycle(world);
    Util.startDaemonThread(treesLifecycle, "Trees Worker");

    AnimalsLifecycle animalsLifecycle = new AnimalsLifecycle(world);
    Util.startDaemonThread(animalsLifecycle, "Animals Worker");

    AnimalsMovement animalsMovement = new AnimalsMovement(world);
    Util.startDaemonThread(animalsMovement, "Animals Movement");
  }
}
