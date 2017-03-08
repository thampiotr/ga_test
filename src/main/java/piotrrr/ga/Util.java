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
    double fpValue = random.nextGaussian() * stDev + mean;
    int intValue = Double.valueOf(fpValue).intValue();
    if (fpValue - intValue >= 0.5) { // Round half up.
      ++intValue;
    }
    if (intValue < min || intValue >= max) {
      // try again
      return randomNormal(random, mean, stDev, min, max);
    }
    return intValue;
  }

  public static void startDaemonThread(Runnable r, String name) {
    Thread thread = new Thread(r, name);
    thread.setDaemon(true);
    thread.start();
  }
}
