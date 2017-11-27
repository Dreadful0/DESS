/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import PetriObj.PetriObjModel;

import java.util.ArrayList;

/**
 * @author masha
 */
public abstract class EAOptimizer {

    private final PetriObjModel initialModel;

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
     */
    public EAOptimizer(PetriObjModel initialModel) {
        this.initialModel = initialModel;
        // set default parameters
        this.populationSize = 10;
        this.generationsNumber = 10;
        this.elitismProbability = 0.2;
        this.mutationProbability = 0.8;
        this.crossoverProbability = 0;
        this.mutationRange = 0.5;
    }

    public PetriObjModel getInitialModel() {
        return initialModel;
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
        this.elitismProbability = elitismProbability;
        this.mutationProbability = mutationProbability;
        this.crossoverProbability = crossoverProbability;
        this.mutationRange = mutationRange;
    }

    private void generatePopulation() {
        population = new ArrayList<>();
        // TODO
        for (int i = 0; i < populationSize; i++) {
            population.add(initialModel);
        }
    }

    private ArrayList<PetriObjModel> elitism() {
        ArrayList<PetriObjModel> eliteIndividuums = new ArrayList<>();
        int number = (int) (population.size() * elitismProbability);
        // TODO
        for (int i = 0; i < number; i++) {
            eliteIndividuums.add(initialModel);
        }
        return eliteIndividuums;
    }

    private ArrayList<PetriObjModel> mutation() {
        ArrayList<PetriObjModel> mutatedIndividuums = new ArrayList<>();
        int number = (int) (population.size() * mutationProbability);
        for (int i = 0; i < number; i++) {
            PetriObjModel current = population.get((int) Math.floor(Math.random() * populationSize));
            current.getListObj().forEach(petriSim -> petriSim.mutate(mutationRange));
            mutatedIndividuums.add(current);
        }
        return mutatedIndividuums;
    }

    protected abstract ArrayList<PetriObjModel> crossover();

    public abstract double fitnessFunction(PetriObjModel model);

    public PetriObjModel evolve() {
        generatePopulation();

        for (int i = 0; i < generationsNumber; i++) {

            ArrayList<PetriObjModel> eliteIndividuums = elitism();
//            System.out.format("Elite size: %d\n", eliteIndividuums.size());
            System.out.format("Generation: %d. Best result: %f\n", i, fitnessFunction(eliteIndividuums.get(0)));

            ArrayList<PetriObjModel> mutatedIndividuums = mutation();
//            System.out.format("Mutated size: %d\n", mutatedIndividuums.size());

            ArrayList<PetriObjModel> crossoverIndividuums = null;
            if (crossoverProbability != 0) {
                crossoverIndividuums = crossover();
//                System.out.format("Crossover size: %d\n", crossoverIndividuums.size());
            }

            population = new ArrayList<>();
            population.addAll(eliteIndividuums);
            population.addAll(mutatedIndividuums);
            if (crossoverProbability != 0) {
                population.addAll(crossoverIndividuums);
            }
//            System.out.format("Population size: %d\n", population.size());
        }

        return population.get(0);
    }

}
