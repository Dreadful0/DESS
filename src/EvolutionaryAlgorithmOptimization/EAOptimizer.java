/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import PetriObj.PetriObjModel;
import java.util.ArrayList;

/**
 *
 * @author masha
 */
public abstract class EAOptimizer {
        
    private final PetriObjModel initialModel;
    
    private ArrayList<PetriObjModel> population;
    private int populationSize;
    private int generationsNumber;
    
    private double elitismProbability;
    private double tournamentWinnerProbability;
    private double mutationProbability;
    private double crossoverProbability;

    /**
     * Create optimization tool for PetriObjModel.
     * Default population size = 10, generations number = 10. Set other values if needed.
     * In order to use crossover method override it.
     * @param initialModel
     */
    public EAOptimizer(PetriObjModel initialModel) {
        this.initialModel = initialModel;
        // set default parameters
        this.populationSize = 10;
        this.generationsNumber = 10;
        this.elitismProbability = 0.2;
        this.tournamentWinnerProbability = 0.75;
        this.mutationProbability = 0.8;
        this.crossoverProbability = 0;
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
     * @param elitismProbability
     * @param mutationProbability
     * @param crossoverProbability
     * @throws Exception if sum of probabilities doesn't equal to 1.00
     */
    public void setProbabilities(double elitismProbability, double mutationProbability, double crossoverProbability, double tournamentWinnerProbability) throws Exception{
        if (elitismProbability + mutationProbability + crossoverProbability != 1){
            throw new Exception("Sum of probabilities doesn't equal to 1.00");
        }
        this.elitismProbability = elitismProbability;
        this.tournamentWinnerProbability = tournamentWinnerProbability;
        this.mutationProbability = mutationProbability;
        this.crossoverProbability = crossoverProbability;
    }
    
    private void generatePopulation(){
        population = new ArrayList<>();
        // TODO
        for (int i = 0; i < populationSize; i++){
            population.add(initialModel);
        }
    }
    
    private ArrayList<PetriObjModel> elitism(){
        ArrayList<PetriObjModel> elite_individuums = new ArrayList<>();
        int number = (int) (population.size() * elitismProbability);
        // TODO
        for (int i = 0; i < number; i++){
            elite_individuums.add(initialModel);
        }
        return elite_individuums;
    }
    
    private ArrayList<PetriObjModel> mutation(){
        ArrayList<PetriObjModel> mutated_individuums = new ArrayList<>();
        int number = (int) (population.size() * mutationProbability);
        // TODO
        for (int i = 0; i < number; i++){
            mutated_individuums.add(initialModel);
        }
        return mutated_individuums;
    }
    
    protected abstract ArrayList<PetriObjModel> crossover();
    
    public abstract double fitness_function(PetriObjModel model);
    
    public PetriObjModel evolve(){
        generatePopulation();
        
        for (int i = 0; i < generationsNumber; i++){
            
            ArrayList<PetriObjModel> elite_individuums = elitism();
//            System.out.format("Elite size: %d\n", elite_individuums.size());
            System.out.format("Generation: %d. Best result: %f\n", i, fitness_function(elite_individuums.get(0)));
            
            ArrayList<PetriObjModel> mutated_individuums = mutation();
//            System.out.format("Mutated size: %d\n", mutated_individuums.size());
            
            ArrayList<PetriObjModel> crossover_individuums = null;
            if (crossoverProbability != 0){
                crossover_individuums = crossover();
//                System.out.format("Crossover size: %d\n", crossover_individuums.size());
            }
            
            population = new ArrayList<>();
            population.addAll(elite_individuums);
            population.addAll(mutated_individuums);
            if (crossoverProbability != 0){
                population.addAll(crossover_individuums);
            }
//            System.out.format("Population size: %d\n", population.size());
        }
        
        return population.get(0);
    }
    
}
