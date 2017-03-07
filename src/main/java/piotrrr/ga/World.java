package piotrrr.ga;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import piotrrr.ga.schema.Entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class World {
  private int width = 500;
  private int height = 500;
  private long timeTick = 100;
  private LinkedList<Consumer<Entity>> addEntityObservers = new LinkedList<>();
  private LinkedList<Consumer<Entity>> removeEntityObservers = new LinkedList<>();

  private Map<Integer, Multimap<Integer, Entity>> entities = new ConcurrentHashMap<>();

  public World() {
    for (int x = 0; x < width; x++) {
      entities.put(x, Multimaps.synchronizedMultimap(HashMultimap.create()));
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void addEntity(Entity e) {
    Multimap<Integer, Entity> column = entities.get(getXWithWraparound(e));
    column.put(getYWithWraparound(e), e);
    addEntityObservers.forEach(c -> c.accept(e));
  }

  public List<Entity> getEntitiesAt(int x, int y) {
    LinkedList<Entity> result = null;
    try {
      y = wrapY(y);
      x = wrapX(x);
      result = new LinkedList<>();
      Multimap<Integer, Entity> column = entities.get(x);
      Collection<Entity> entities = column.get(y);
      entities.forEach(result::add);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public void removeEntity(Entity e) {
    Multimap<Integer, Entity> column = entities.get(getXWithWraparound(e));
    if (column.remove(getYWithWraparound(e), e)) {
      removeEntityObservers.forEach(c -> c.accept(e));
    }
  }

  private int getYWithWraparound(Entity e) {
    return wrapY(e.getPosition().getY());
  }

  private int getXWithWraparound(Entity e) {
    return wrapX(e.getPosition().getX());
  }

  private int wrapY(int y) {
    return Math.abs(y % height);
  }

  private int wrapX(int x) {
    return Math.abs(x % width);
  }

  public void forAllEntities(Consumer<Entity> consumer) {
    entities.values().forEach(column -> column.values().forEach(consumer));
  }
}

