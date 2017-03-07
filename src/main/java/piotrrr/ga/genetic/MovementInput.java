package piotrrr.ga.genetic;

import lombok.AllArgsConstructor;
import lombok.Data;
import piotrrr.ga.schema.ObjectType;

@Data
@AllArgsConstructor
public class MovementInput {
  private byte state;
  private ObjectType objectInFront;
}
