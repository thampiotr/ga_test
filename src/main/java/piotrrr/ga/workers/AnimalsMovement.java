package piotrrr.ga.workers;

import piotrrr.ga.World;
import piotrrr.ga.genetic.Action;
import piotrrr.ga.genetic.MovementInput;
import piotrrr.ga.schema.*;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AnimalsMovement implements Runnable {
  private World world;
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
      for (Animal animal : managedAnimals) {
        ObjectType objectInFront = determineObjectInFront(animal);
        MovementInput input = new MovementInput(animal.getState(), objectInFront);
        Action action = animal.getGenome().getNextAction(input);
        action.perform(world, animal);
      }
      // Clear before sleeping to free up some memory.
      managedAnimals.clear();
      try {
        Thread.sleep(world.getTimeTick());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private ObjectType determineObjectInFront(Animal animal) {
    Position positionInFront = animal.getPositionInFront();
    if (!world.isWithinBounds(positionInFront)) {
      return ObjectType.WALL;
    }
    Set<Class> classSet = world.getEntitiesAt(positionInFront)
        .stream().map(Entity::getClass).collect(Collectors.toSet());
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
}
