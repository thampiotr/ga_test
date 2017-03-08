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
  private HashSet<Animal> managedAnimals = new HashSet<>();

  public AnimalsMovement(World world) {
    this.world = world;
  }

  @Override
  public void run() {
    while (true) {
      // managedTrees is empty at the beginning of each iteration.
      world.forAllEntities(entity -> {
        if (entity instanceof Animal) {
          managedAnimals.add((Animal) entity);
        }
      });

      runOnce();

      // Clear before sleeping to free up some memory.
      managedAnimals.clear();
      try {
        Thread.sleep(world.getTimeTick());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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
//    double probabilityOfMovingForward = 0.8;
//    double turningProbability = 1 - probabilityOfMovingForward;
//    double chance = random.nextDouble();
//    if (chance <= probabilityOfMovingForward) {
//      return Action.MOVE_FORWARD;
//    } else if (chance <= turningProbability / 2) {
//      return Action.TURN_LEFT;
//    } else {
//      return Action.TURN_RIGHT;
//    }
    double chance = random.nextInt(3);
    if (chance == 0) {
      return Action.MOVE_FORWARD;
    } else if (chance == 1) {
      return Action.TURN_LEFT;
    } else {
      return Action.TURN_RIGHT;
    }
  }
}
