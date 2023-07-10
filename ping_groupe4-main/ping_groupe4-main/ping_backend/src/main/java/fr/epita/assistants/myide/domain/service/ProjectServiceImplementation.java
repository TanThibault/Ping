package fr.epita.assistants.myide.domain.service;



import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.AspectImplementation;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.NodeImplementation;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.ProjectImplementation;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectServiceImplementation implements ProjectService {

    private NodeService nodeService;
    private NodeService tmpNodeService;

    public ProjectServiceImplementation(Path root, Path tmp) {
        File rootDir = root.toFile();
        if (!rootDir.exists()) {
                rootDir.mkdir();
        }
        nodeService = new NodeServiceImplementation(new NodeImplementation(root));

        File tmpDir = tmp.toFile();
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        tmpNodeService = new NodeServiceImplementation(new NodeImplementation(tmp));
    }

    @Override
    public Project load(Path root) {
        File rootDir = root.toFile();
        if (!rootDir.exists())
            rootDir.mkdir();
        // rootNode
        /*Node rootNode = ((NodeServiceImplementation) nodeService).getRoot();//new NodeImplementation(root, rootDir.isDirectory() ? Node.Types.FOLDER : Node.Types.FILE);
        Queue<Node> nodes = new LinkedList<Node>();
        nodes.add(rootNode);
        while ( !nodes.isEmpty() ) {
            Node dir = nodes.remove();

            for ( var file : dir.getPath().toFile().listFiles() ) {
                // System.out.println(file.getPath());
                if (file.isDirectory())
                {
                    Node newNode = nodeService.create(dir, file.getName(), Node.Types.FOLDER);
                    nodes.add(newNode);
                }
                else
                    nodeService.create(dir, file.getName(), Node.Types.FILE);
            }

        }*/
        Set<Aspect> aspects = Stream.of(Objects.requireNonNull(rootDir.listFiles())).filter(file -> file.getName().equals(".git") || file.getName().equals("pom.xml") || file.getName().equals("package.json")).map(file ->
        {
            if(file.getName().equals(".git"))
                return new AspectImplementation(AspectImplementation.Aspects.GIT);
            else if(file.getName().equals("pom.xml"))
                return new AspectImplementation(AspectImplementation.Aspects.MAVEN);
            else
                return new AspectImplementation(AspectImplementation.Aspects.NODE);
        }).collect(Collectors.toSet());
        aspects.add(new AspectImplementation(AspectImplementation.Aspects.ANY));
        // project
        return new ProjectImplementation(new NodeImplementation(root), aspects);
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        return project.getFeature(featureType).get().execute(project, params);
    }

    @Override
    public NodeService getNodeService() {
        return nodeService;
    }

    @Override
    public NodeService getTmpNodeService() {
        return tmpNodeService;
    }
}