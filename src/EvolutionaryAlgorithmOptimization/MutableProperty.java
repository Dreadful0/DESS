package EvolutionaryAlgorithmOptimization;

/**
 * @author lidaamber
 */
public class MutableProperty {
    public MutableHolder mutableProperty;
    public int propertyKey;
    public double mutationRange;

    public MutableProperty(MutableHolder mutableProperty, int propertyKey, double mutationRange) {
        this.mutableProperty = mutableProperty;
        this.propertyKey = propertyKey;
        this.mutationRange = mutationRange;
    }

}
