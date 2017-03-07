package piotrrr.ga.genetic;

import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

public interface Action {

  void perform(World world, Animal animal);

  Action TURN_LEFT = (world, animal) -> animal.setOrientation(animal.getOrientation().turnLeft());

  Action TURN_RIGHT = (world, animal) -> animal.setOrientation(animal.getOrientation().turnRight());

  Action MOVE_FORWARD = (world, animal) -> {
    Position newPosition = animal.getPositionInFront();
    if (world.isWithinBounds(newPosition)) {
      // Eat all the trees.
      world.getEntitiesAt(newPosition).stream()
          .filter(e -> e instanceof Tree)
          .forEach(world::removeEntity);
      world.removeEntity(animal);
      animal.setPosition(newPosition);
      world.addEntity(animal);
    }
  };

  default ChangeState changeStateTo(byte newState) {
    return new ChangeState(newState);
  }

  class ChangeState implements Action {
    private final byte newState;

    public ChangeState(byte newState) {
      this.newState = newState;
    }

    @Override
    public void perform(World world, Animal animal) {
      animal.setState(newState);
    }
  }
}
