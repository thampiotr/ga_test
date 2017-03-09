package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Data;
import piotrrr.ga.genetic.Action;
import piotrrr.ga.genetic.MovementInput;

import java.util.Random;

@Data
@Builder
public class Genome implements Component {
  public static final int GENOME_STATE_SIZE = 16;
  private static final int GENOME_LENGTH = 64;
  private static final int POSSIBLE_OUTPUTS = 48;

  private byte[] data = new byte[GENOME_LENGTH];

  public Action getNextAction(MovementInput input) {
    int genomePart = input.getObjectInFront().genomePart;
    int partSize = GENOME_LENGTH / ObjectType.values().length;
    byte response = data[genomePart * partSize + input.getState()];
    byte newState = (byte) (response & 0x0f);
    int newMove = (response & 0xf0) % 3;
    if (newMove == 0) {
      return Action.turnLeftAndChangeState(newState);
    } else if (newMove == 1) {
      return Action.turnRightAndChangeState(newState);
    } else {
      return Action.moveForwardAndChangeState(newState);
    }
  }

  public static byte[] randomBytes(Random random) {
    byte[] bytes = new byte[GENOME_LENGTH];
    random.nextBytes(bytes);
    return bytes;
  }
}
