package fr.epita.assistants.response;

import fr.epita.assistants.myide.domain.entity.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
public class NodeResponse {
    public String path;
    public Node.Type type;
    public List<NodeResponse> children;

    public static NodeResponse response(Node n) {
        List<NodeResponse> p = new ArrayList<>();
        for (int i = 0; i < n.getChildren().size(); i++) {
            p.add(NodeResponse.response(n.getChildren().get(i)));
        }
        return new NodeResponse(n.getPath().toString(),n.getType(),p);
    }
}
