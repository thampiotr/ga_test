package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Animal implements Entity {
  private Position position;
  private long bornTime;
}

