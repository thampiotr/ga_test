package piotrrr.ga.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Tree implements Entity {
    private Position position;
    private long bornTime;
}
