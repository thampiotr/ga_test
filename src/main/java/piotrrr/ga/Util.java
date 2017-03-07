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
  public static int randomNormal(Random random, double mean, double stDev, int min, int max) {
    int number = Double.valueOf(random.nextGaussian() * stDev + mean).intValue();
    if (number < min || number >= max) {
      // try again
      return randomNormal(random, mean, stDev, min, max);
    }
    return number;
  }

  public static void startDaemonThread(Runnable r, String name) {
    Thread thread = new Thread(r, name);
    thread.setDaemon(true);
    thread.start();
  }
}
