package piotrrr.ga.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Bush implements HasPosition {
    private Position position;
    private long lastReproduced;
}
