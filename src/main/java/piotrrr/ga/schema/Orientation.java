package piotrrr.ga.schema;

public enum Orientation {
  NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);

  public final int dx;
  public final int dy;

  Orientation(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  public Orientation turnLeft() {
    switch (this) {
      case NORTH:
        return WEST;
      case WEST:
        return SOUTH;
      case SOUTH:
        return EAST;
      case EAST:
        return NORTH;
    }
    throw new IllegalArgumentException("Can't turn left: " + this);
  }

  public Orientation turnRight() {
    switch (this) {
      case NORTH:
        return EAST;
      case EAST:
        return SOUTH;
      case SOUTH:
        return WEST;
      case WEST:
        return NORTH;
    }
    throw new IllegalArgumentException("Can't turn right: " + this);
  }

}
