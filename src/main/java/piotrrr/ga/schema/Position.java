package piotrrr.ga.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Position {
    private long x;
    private long y;
    private long z;
}
