package fr.epita.assistants.myide.domain.entity;

import javax.validation.Path;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectImplementation implements Project{

    private Node RootNode;
    private Set<Aspect> Aspects;

    public ProjectImplementation(Node rootNode,Set<Aspect> aspects)
    {
        RootNode = rootNode;
        Aspects = aspects;

    }
    @Override
    public Node getRootNode() {
        return RootNode;
    }

    @Override
    public Set<Aspect> getAspects() {
        return Aspects;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        return getAspects().stream().map(Aspect::getFeatureList).flatMap(Collection::stream).filter(feature -> feature.type().toString().equals(featureType.toString())).findFirst();
    }
}
