package piotrrr.ga.workers;

import piotrrr.ga.World;
import piotrrr.ga.genetic.Action;
import piotrrr.ga.genetic.MovementInput;
import piotrrr.ga.schema.*;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class AnimalsMovement implements Runnable {
  private World world;
  private final Random random = new Random();
  private HashSet<Animal> managedAnimals;

  public AnimalsMovement(World world, HashSet<Animal> managedAnimals) {
    this.world = world;
    this.managedAnimals = managedAnimals;
  }

  @Override
  public void run() {
    throw new UnsupportedOperationException("Nope.");
  }

  public void runOnce() {
    for (Animal animal : managedAnimals) {
      ObjectType objectInFront = determineObjectInFront(animal);
      MovementInput input = new MovementInput(animal.getState(), objectInFront);
      Action action = getNextAction(input, animal.getGenome());
      action.perform(world, animal);
    }
  }

  private ObjectType determineObjectInFront(Animal animal) {
    Position positionInFront = animal.getPositionInFront();
    if (!world.isWithinBounds(positionInFront)) {
      return ObjectType.WALL;
    }
    Set<Class> classSet =
        world.getEntitiesAt(positionInFront).stream().map(Entity::getClass).collect(Collectors.toSet());
    if (classSet.contains(Animal.class)) {
      return ObjectType.ANIMAL;
    } else if (classSet.contains(Tree.class)) {
      return ObjectType.TREE;
    } else if (classSet.isEmpty()) {
      return ObjectType.EMPTY;
    }
    throw new InvalidStateException(
        "Cannot determine what's in front! Found: " + classSet + ", position: " + positionInFront);
  }


  private Action getNextAction(MovementInput input, Genome genome) {
    int i = random.nextInt(100);
    if (i < 96) {
      return Action.MOVE_FORWARD;
    } else if (i < 98) {
      return Action.TURN_LEFT;
    } else {
      return Action.TURN_RIGHT;
    }
  }
}
