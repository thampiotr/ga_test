package piotrrr.ga.schema;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;


@Getter
public class Cell {
  private Position position;
  private Map<Class, Object> components = new LinkedHashMap<>();
}
