package fr.epita.assistants.request;

import fr.epita.assistants.myide.domain.entity.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoveNodeRequest {
    public String nodeToMove;
    public String destinationFolder;
}
