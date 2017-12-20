package EvolutionaryAlgorithmOptimization;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lidaamber
 */
public class MutationBuilder {

    private List<MutableProperty> properties;

    MutationBuilder() {
        properties = new ArrayList<>();
    }

    public MutationBuilder add(MutableProperty property) {
        properties.add(property);
        return this;
    }

    List<MutableProperty> build() {
        return properties;
    }
}
