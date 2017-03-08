package piotrrr.ga.genetic;

import piotrrr.ga.World;
import piotrrr.ga.schema.Animal;
import piotrrr.ga.schema.Entity;
import piotrrr.ga.schema.Position;
import piotrrr.ga.schema.Tree;

import java.util.List;
import java.util.stream.Collectors;

public interface Action {

  void perform(World world, Animal animal);

  static Action turnLeftAndChangeState(byte newState) {
    return (world, animal) -> {
      animal.setState(newState);
      animal.setOrientation(animal.getOrientation().turnLeft());
    };
  }

  static Action turnRightAndChangeState(byte newState) {
    return (world, animal) -> {
      animal.setState(newState);
      animal.setOrientation(animal.getOrientation().turnRight());
    };
  }

  static Action moveForwardAndChangeState(byte newState) {
    return (world, animal) -> {
      animal.setState(newState);
      Position newPosition = animal.getPositionInFront();
      if (world.isWithinBounds(newPosition)) {
        if (world.removeEntity(animal)) { // If failed to remove, it may mean the animal has died. Do not add it back.
          // Eat all the trees.
          List<Entity> trees = world.getEntitiesAt(newPosition).stream()
              .filter(e -> e instanceof Tree).collect(Collectors.toList());
          trees.forEach(world::removeEntity);
          animal.setTreesEaten(trees.size() + animal.getTreesEaten());
          animal.setPosition(newPosition);
          world.addEntity(animal);
        }
      }
    };
  }
}
