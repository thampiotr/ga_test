package piotrrr.ga;

import java.util.Random;

public class Util {
  /**
   * Normal distribution.
   *
   * @param random random to use
   * @param mean   mean
   * @param stDev  standard deviation
   * @param min    minimum (inclusive)
   * @param max    maximum (exclusive)
   * @return pseudo-random number following a normal distribution with the above parameters.
   */
  static int randomNormal(Random random, double mean, double stDev, int min, int max) {
    return Math.min(max - 1, Math.max(min, (int) (random.nextGaussian() * stDev + mean)));
  }

  public static void startDaemonThread(Runnable r, String name) {
    Thread thread = new Thread(r, name);
    thread.setDaemon(true);
    thread.start();
  }
}
