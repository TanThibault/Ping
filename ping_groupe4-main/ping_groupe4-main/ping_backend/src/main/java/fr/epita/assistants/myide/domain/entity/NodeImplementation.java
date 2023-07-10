package fr.epita.assistants.myide.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Objects;

public class NodeImplementation implements Node{
    private Path path;
    private Types type;
    private List<@NotNull Node> children;

    public NodeImplementation(Path path)
    {
        this.path = path;
        this.children = new ArrayList<>();
        if(path.toFile().isDirectory())
        {
            this.type = Types.FOLDER;
            for(File p : Objects.requireNonNull(path.toFile().listFiles()))
            {
                this.children.add(new NodeImplementation(p.toPath()));
            }
        }
        else
            this.type = Types.FILE;
    }

    @Override
    public @NotNull Path getPath()
    {
        return path;
    }

    @Override
    public @NotNull Type getType()
    {
        return type;
    }

    @Override
    public @NotNull List<@NotNull Node> getChildren()
    {
        if (type == Types.FILE)
            return new ArrayList<>();
        return children;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public void addChildren(Node child) {
        this.children.add(child);
    }
}
