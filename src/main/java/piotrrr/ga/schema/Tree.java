package piotrrr.ga.schema;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Tree implements Entity {
    private Position position;
}
