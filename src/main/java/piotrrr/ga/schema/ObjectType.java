package piotrrr.ga.schema;

public enum ObjectType {
  WALL(0), ANIMAL(1), TREE(2), EMPTY(3);

  public final int genomePart;

  ObjectType(int genomePart) {
    this.genomePart = genomePart;
  }
}
