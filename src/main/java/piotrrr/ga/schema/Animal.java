package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Animal implements Entity {
  private Position position;
  private long bornTime;
}

