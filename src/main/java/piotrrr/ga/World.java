package piotrrr.ga;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import piotrrr.ga.schema.Entity;
import piotrrr.ga.schema.Position;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class World {
  public static final int WORLD_WIDTH = 500;
  public static final int WORLD_HEIGHT = 500;

  private int width = WORLD_WIDTH;
  private int height = WORLD_HEIGHT;
  private long timeTick = 0;

  private Map<Integer, Multimap<Integer, Entity>> entities = new ConcurrentHashMap<>();

  public World() {
    for (int x = 0; x < width; x++) {
      entities.put(x, Multimaps.synchronizedMultimap(HashMultimap.create()));
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void addEntity(Entity e) {
//    normalize(e);
    Multimap<Integer, Entity> column = entities.get(getXWithWraparound(e));
    column.put(getYWithWraparound(e), e);
  }

  public List<Entity> getEntitiesAt(Position position) {
    return getEntitiesAt(position.getX(), position.getY());
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

  public boolean removeEntity(Entity e) {
    Multimap<Integer, Entity> column = entities.get(getXWithWraparound(e));
    return column.remove(getYWithWraparound(e), e);
  }

  private int getYWithWraparound(Entity e) {
    return wrapY(e.getPosition().getY());
  }

  private int getXWithWraparound(Entity e) {
    return wrapX(e.getPosition().getX());
  }

  private int wrapY(int y) {
    return y % height;
  }

  private int wrapX(int x) {
    return x % width;
  }

  public void forAllEntities(Consumer<Entity> consumer) {
    entities.values().parallelStream().forEach(column -> column.values().forEach(consumer));
  }

  public boolean isWithinBounds(Position position) {
    return position.getX() >= 0 && position.getX() < getWidth()
        && position.getY() >= 0 && position.getY() < getHeight();
  }

}

