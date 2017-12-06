/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import PetriObj.PetriObjModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author masha
 */
public abstract class EAOptimizer {

    private final PetriObjModel initialModel;
    private int timeModeling;
    private OptType optType;

    private ArrayList<PetriObjModel> population;
    private int populationSize;
    private int generationsNumber;

    private double mutationRange;
    private double elitismProbability;
    private double mutationProbability;
    private double crossoverProbability;

    /**
     * Create optimization tool for PetriObjModel.
     * Default population size = 10, generations number = 10. Set other values if needed.
     * In order to use crossover method override it.
     *
     * @param initialModel
     * @param timeModeling
     */
    public EAOptimizer(PetriObjModel initialModel, int timeModeling) {
        this.initialModel = initialModel;
        this.timeModeling = timeModeling;
        // set default parameters
        this.optType = OptType.OPT_MAX;
        this.populationSize = 10;
        this.generationsNumber = 10;
        this.elitismProbability = 0.2;
        this.mutationProbability = 0.8;
        this.crossoverProbability = 0;
        this.mutationRange = 0.5;
    }

    // todo add verbose parameter

    public PetriObjModel getInitialModel() {
        return initialModel;
    }

    public OptType getOptType() {
        return optType;
    }

    public void setOptType(OptType optType) {
        this.optType = optType;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getGenerationsNumber() {
        return generationsNumber;
    }

    public void setGenerationsNumber(int generationsNumber) {
        this.generationsNumber = generationsNumber;
    }

    /**
     * Set probabilities for elitism, mutation and crossover.
     *
     * @param elitismProbability
     * @param mutationProbability
     * @param crossoverProbability
     * @param mutationRange
     * @throws Exception if sum of probabilities doesn't equal to 1.00
     */
    public void setProbabilities(double elitismProbability, double mutationProbability,
                                 double crossoverProbability,
                                 double mutationRange) throws Exception {
        if (elitismProbability + mutationProbability + crossoverProbability != 1) {
            throw new Exception("Sum of probabilities doesn't equal to 1.00");
        }
        // todo set number of individuums instead of probabilities
        this.elitismProbability = elitismProbability;
        this.mutationProbability = mutationProbability;
        this.crossoverProbability = crossoverProbability;
        this.mutationRange = mutationRange;
    }

    private void generatePopulation() throws CloneNotSupportedException {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(initialModel.clone());
        }
    }

    private ArrayList<PetriObjModel> elitism(Comparator<PetriObjModel> comparator) throws CloneNotSupportedException {
        ArrayList<PetriObjModel> eliteIndividuums = new ArrayList<>();
        int number = (int) (population.size() * elitismProbability);
        population.sort(comparator);
        for (int i = 0; i < number; i++) {
            eliteIndividuums.add(population.get(i).clone());
        }

        // todo use verbose parameter
        for (int j = 0; j < population.size(); j++) {
            PetriObjModel model = population.get(j);
            System.out.format("Model: %d. Fitness: %f.\n", j, fitnessFunction(model));
        }
        System.out.format("Best result: %f\n", fitnessFunction(population.get(0)));

        return eliteIndividuums;
    }

    private ArrayList<PetriObjModel> mutation() throws CloneNotSupportedException {
        ArrayList<PetriObjModel> mutatedIndividuums = new ArrayList<>();
        int number = (int) (population.size() * mutationProbability);
        for (int i = 0; i < number; i++) {
            int ind = (int) Math.floor(Math.random() * populationSize);
            PetriObjModel current = population.get(ind).clone();
            current.mutate();
            mutatedIndividuums.add(current);
        }
        return mutatedIndividuums;
    }

    protected abstract ArrayList<PetriObjModel> crossover();

    public abstract double fitnessFunction(PetriObjModel model);

    public PetriObjModel evolve() throws CloneNotSupportedException {

        Comparator<PetriObjModel> comparator = (left, right) -> {
            double leftRank = fitnessFunction(left);
            double rightRank = fitnessFunction(right);
            if (optType == OptType.OPT_MAX) {
                return Double.compare(rightRank, leftRank);
            } else {
                return Double.compare(leftRank, rightRank);
            }
        };

        generatePopulation();

        for (int i = 0; i < generationsNumber; i++) {

            for (PetriObjModel model : population) {
                model.go(timeModeling);
            }

            System.out.format("Generation: %d.\n", i);
            ArrayList<PetriObjModel> eliteIndividuums = elitism(comparator);

            ArrayList<PetriObjModel> mutatedIndividuums = mutation();

            ArrayList<PetriObjModel> crossoverIndividuums = null;
            if (crossoverProbability != 0) {
                crossoverIndividuums = crossover();
            }

            population = new ArrayList<>();
            population.addAll(eliteIndividuums);
            population.addAll(mutatedIndividuums);
            if (crossoverProbability != 0) {
                population.addAll(crossoverIndividuums);
            }

        }

        return population.get(0);
    }

    void setMutableProperties(List<MutableProperty> properties) {
        properties.forEach(initialModel::addMutableProperty);
    }
}
