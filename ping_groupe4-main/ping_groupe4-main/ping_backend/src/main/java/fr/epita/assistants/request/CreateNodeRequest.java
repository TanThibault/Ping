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
public class CreateNodeRequest {
    public String folder;
    public String name;
    public String type;
}
