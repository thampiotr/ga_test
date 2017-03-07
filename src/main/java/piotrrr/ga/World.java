package piotrrr.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import piotrrr.ga.schema.Bush;
import piotrrr.ga.schema.HasPosition;

import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@NoArgsConstructor
public class World {
    private int width = 500;
    private int height = 500;
    private long timeTick = 100L;

    private ConcurrentLinkedDeque<Bush> bushes = new ConcurrentLinkedDeque<>();
    private HasPosition[][] cells = new HasPosition[width][height];

    public long getCurrentTimeInTicks() {
        return System.currentTimeMillis() / timeTick;
    }
}

