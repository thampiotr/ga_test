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
  private long timeTick = 10L;
  private LinkedList<Consumer<Entity>> addEntityObservers = new LinkedList<>();

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
      y = y % height;
      x = x % width;
      result = new LinkedList<>();
      Multimap<Integer, Entity> column = entities.get(x);
      Collection<Entity> entities = column.get(y);
      entities.forEach(result::add);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  private int getYWithWraparound(Entity e) {
    return e.getPosition().getY() % height;
  }

  private int getXWithWraparound(Entity e) {
    return e.getPosition().getX() % width;
  }

  public void forAllEntities(Consumer<Entity> consumer) {
    entities.values().forEach(column -> column.values().forEach(consumer));
  }

  public long getCurrentTimeInTicks() {
    return System.currentTimeMillis() / timeTick;
  }
}

