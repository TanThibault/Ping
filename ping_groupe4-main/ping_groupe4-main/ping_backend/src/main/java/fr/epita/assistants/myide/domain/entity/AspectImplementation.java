package fr.epita.assistants.myide.domain.entity;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class AspectImplementation implements Aspect {
    private Aspects type;
    private List<Feature> featureList;

    public AspectImplementation(Aspects type)
    {
        this.type = type;
        List<Feature> featureList = new ArrayList<>();
        if (type == Aspects.GIT)
        {
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Git.ADD));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Git.COMMIT));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Git.PULL));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Git.PUSH));
        }
        else if (type == Aspects.MAVEN)
        {
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.CLEAN));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.COMPILE));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.EXEC));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.TEST));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.PACKAGE));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.TREE));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Maven.INSTALL));
        }
        else if (type == Aspects.NODE)
        {
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Npm.EXE));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Npm.REMOVE));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Npm.INSTALL));
        }
        else
        {
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Any.DIST));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Any.CLEANUP));
            featureList.add(new FeatureImplementation(FeatureImplementation.Features.Any.SEARCH));
        }
        this.featureList = featureList;
    }

    @Override
    public Aspects getType() {
        return type;
    }

    @Override
    public @NotNull List<Feature> getFeatureList() {
        return featureList;
    }

    public enum Aspects implements Type {
        ANY,
        MAVEN,
        GIT,
        NODE
    }
}
