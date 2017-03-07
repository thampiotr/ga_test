package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Position implements Component {
  private int x;
  private int y;
  private int z;
}
