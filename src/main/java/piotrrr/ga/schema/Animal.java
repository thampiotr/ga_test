package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Animal implements Entity {
  private Position position;
  private long bornTime;
  private long treesEaten = 0;
  private byte state = 0;
  private Orientation orientation = Orientation.NORTH;
  private Genome genome = new Genome();

  public double getScore() {
    return 0;
  }

  public Position getPositionInFront() {
    Orientation orientation = getOrientation();
    return Position.builder()
        .x(getPosition().getX() + orientation.dx)
        .y(getPosition().getY() + orientation.dy)
        .z(getPosition().getZ())
        .build();
  }

}

