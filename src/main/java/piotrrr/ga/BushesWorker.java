package piotrrr.ga;

import lombok.AllArgsConstructor;
import piotrrr.ga.schema.Bush;
import piotrrr.ga.schema.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class BushesWorker implements Runnable {

    private static final long INITIAL_POPULATION_SIZE = 20;
    private static final double GROWTH_RATE = 0.001;
    private static final int ST_DEV_GROWN_POSITION = 10;
    private World world;
    private final Random random = new Random();

    @Override
    public void run() {
        createInitialPopulation();
        while (true) {
            Random r = new Random();
            List<Bush> bushesToAdd = new LinkedList<>();
            for (Bush bush : world.getBushes()) {
                double timeSinceLastReproduced = world.getCurrentTimeInTicks() - bush.getLastReproduced();
                // Exponential distribution
                double probabilityOfReproducing = 1.0 - Math.exp(-GROWTH_RATE * timeSinceLastReproduced);
                double chance = r.nextDouble();
                if (chance < probabilityOfReproducing) {
                    bush.setLastReproduced(world.getCurrentTimeInTicks());
                    bushesToAdd.add(newRandomBush(bush.getPosition().getX(), bush.getPosition().getY(), ST_DEV_GROWN_POSITION));
                    System.out.println("Reproducing! probabilityOfReproducing = " + probabilityOfReproducing);
                }
            }
            world.getBushes().addAll(bushesToAdd);
            try {
                Thread.sleep(world.getTimeTick());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createInitialPopulation() {
        for (int i = 0; i < INITIAL_POPULATION_SIZE; i++) {
            long center = world.getWidth() / 2;
            long stDev = world.getWidth() / 5;
            world.getBushes().add(
                    newRandomBush(center, center, stDev)
            );
        }
    }

    private Bush newRandomBush(long meanX, double meanY, long stDev) {
        return Bush.builder()
                .lastReproduced(world.getCurrentTimeInTicks())
                .position(
                        Position.builder()
                                .x((long) (random.nextGaussian() * stDev + meanX))
                                .y((long) (random.nextGaussian() * stDev + meanY))
                                .z(0L)
                                .build())
                .build();
    }
}
