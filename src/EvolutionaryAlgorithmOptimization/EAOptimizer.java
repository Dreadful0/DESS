/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import PetriObj.PetriObjModel;
import PetriObj.PetriSim;

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
    private double elitismProbability = 0.2;
    private int elitismNumber;
    private double mutationProbability = 0.4;
    private int mutationNumber;
    private double crossoverProbability = 0.4;
    private int crossoverNumber;
    private int biasedCrossoverNumber;
    private double tournamentProbability;

    private boolean verbose;

    private Comparator<PetriObjModel> comparator;

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
        this.mutationRange = 0.5;
        this.tournamentProbability = 0.75;
        recountPartNumbers();
    }

    private void recountPartNumbers() {
        this.crossoverNumber = (int) (crossoverProbability * populationSize);
        this.biasedCrossoverNumber = crossoverNumber % 2 == 0 ? crossoverNumber : crossoverNumber + 1;
        this.mutationNumber = (int) (mutationProbability * populationSize);
        this.elitismNumber = populationSize - mutationNumber - crossoverNumber;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

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
        recountPartNumbers();
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
     * @throws Exception if any probability less than 0
     */
    public void setProbabilities(double elitismProbability, double mutationProbability,
                                 double crossoverProbability) throws Exception {
        if (elitismProbability < 0 || mutationProbability < 0 || crossoverProbability < 0) {
            throw new Exception("Probability can't be less than 0");
        }
        if (elitismProbability + mutationProbability + crossoverProbability != 1) {
            double diff = 1 / (elitismProbability + mutationProbability + crossoverProbability);
            elitismProbability *= diff;
            mutationProbability *= diff;
            crossoverProbability *= diff;
        }
        this.elitismProbability = elitismProbability;
        this.mutationProbability = mutationProbability;
        this.crossoverProbability = crossoverProbability;
        recountPartNumbers();
    }

    public void setMutationRange(double mutationRange) {
        this.mutationRange = mutationRange;
    }

    public void setTournamentProbability(double tournamentProbability) {
        this.tournamentProbability = tournamentProbability;
    }

    private void generatePopulation() throws CloneNotSupportedException {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(initialModel.clone());
        }
    }

    private ArrayList<PetriObjModel> elitism() throws CloneNotSupportedException {
        ArrayList<PetriObjModel> eliteIndividuums = new ArrayList<>();
        population.sort(comparator);
        for (int i = 0; i < elitismNumber; i++) {
            eliteIndividuums.add(population.get(i).clone());
        }

        if (verbose) {
            for (int j = 0; j < population.size(); j++) {
                PetriObjModel model = population.get(j);
                System.out.format("Model: %d. Fitness: %f.\n", j, fitnessFunction(model));
            }
            System.out.format("Best result: %f\n", fitnessFunction(population.get(0)));
        }

        return eliteIndividuums;
    }

    private ArrayList<PetriObjModel> mutation() throws CloneNotSupportedException {
        ArrayList<PetriObjModel> mutatedIndividuums = new ArrayList<>();
        for (int i = 0; i < mutationNumber; i++) {
            int ind = (int) Math.floor(Math.random() * populationSize);
            PetriObjModel current = population.get(ind).clone();
            current.mutate();
            mutatedIndividuums.add(current);
        }
        return mutatedIndividuums;
    }

    private ArrayList<PetriObjModel> crossover() throws CloneNotSupportedException {
        int numberParticipants = populationSize / biasedCrossoverNumber;
        ArrayList<PetriObjModel> tournamentWinners = new ArrayList<>();
        for (int i = 0; i < biasedCrossoverNumber; i++) {
            PetriObjModel winner = tournamentWinner(new ArrayList<>(population.subList(i * numberParticipants, (i + 1) * numberParticipants)));
            tournamentWinners.add(winner.clone());
        }
        ArrayList<PetriObjModel> children = new ArrayList<>();
        for (int i = 0; i < tournamentWinners.size(); i++) {
            if (i % 2 == 0) {
                children.addAll(produceChildren(tournamentWinners.get(i), tournamentWinners.get(i + 1)));
            }
        }
        return new ArrayList<>(children.subList(0, crossoverNumber));
    }

    private PetriObjModel tournamentWinner(ArrayList<PetriObjModel> participants) throws CloneNotSupportedException {
        participants.sort(comparator);
        double current_probability = tournamentProbability;
        for (PetriObjModel model : participants) {
            double current = Math.random();
            if (current < current_probability) {
                return model;
            }
            current_probability *= (1 - tournamentProbability);
        }
        return participants.get(participants.size() - 1);
    }

    private ArrayList<PetriObjModel> produceChildren(PetriObjModel parent1, PetriObjModel parent2) throws CloneNotSupportedException {
        ArrayList<PetriObjModel> children = new ArrayList<>();
        ArrayList<PetriSim> listParent1 = parent1.getListObj();
        ArrayList<PetriSim> listParent2 = parent2.getListObj();
        ArrayList<PetriSim> listChild1 = new ArrayList<>();
        ArrayList<PetriSim> listChild2 = new ArrayList<>();
        int n1 = 0;
        int n2 = 0;
        int total = listParent1.size();
        for (int i = 0; i < total; i++) {
            double rand;
            if (n1 <= total / 2 && n2 <= total / 2) {
                rand = Math.random();
            } else {
                if (n1 > total / 2) {
                    rand = 1;
                } else {
                    rand = 0;
                }
            }
            if (rand < 0.5) {
                n1++;
                listChild1.add(listParent1.get(i).clone());
                listChild2.add(listParent2.get(i).clone());
            } else {
                n2++;
                listChild1.add(listParent2.get(i).clone());
                listChild2.add(listParent1.get(i).clone());
            }
        }

        PetriObjModel child1 = new PetriObjModel(listChild1);
        child1.setShouldGetStatistics(initialModel.isShouldGetStatistics());
        child1.setIsProtoсol(initialModel.isProtocolPrint());
        for (PetriObjModel.LinkByPlaces li : parent1.getLinks()) {
            int one = parent1.getNumInList(li.getOne());
            int other = parent1.getNumInList(li.getOther());
            if (one >= 0 && other >= 0) {
                PetriSim oneClone = child1.getListObj().get(one);
                PetriSim otherClone = child1.getListObj().get(other);
                child1.linkObjectsCombiningPlaces(oneClone, li.getNumPlaceOne(),
                        otherClone, li.getNumPlaceOther());
            }
        }
        parent1.cloneMutableProperties(child1);
        child1.setIsProtoсol(parent1.isProtocolPrint());
        child1.setShouldGetStatistics(parent1.isShouldGetStatistics());
        children.add(child1);


        PetriObjModel child2 = new PetriObjModel(listChild2);
        child2.setShouldGetStatistics(initialModel.isShouldGetStatistics());
        child2.setIsProtoсol(initialModel.isProtocolPrint());
        for (PetriObjModel.LinkByPlaces li : parent1.getLinks()) {
            int one = parent1.getNumInList(li.getOne());
            int other = parent1.getNumInList(li.getOther());

            if (one >= 0 && other >= 0) {
                PetriSim oneClone = child2.getListObj().get(one);
                PetriSim otherClone = child2.getListObj().get(other);
                child2.linkObjectsCombiningPlaces(oneClone, li.getNumPlaceOne(),
                        otherClone, li.getNumPlaceOther());
            }
        }
        parent1.cloneMutableProperties(child2);
        child2.setIsProtoсol(parent1.isProtocolPrint());
        child2.setShouldGetStatistics(parent1.isShouldGetStatistics());
        children.add(child2);

        return children;
    }

    public abstract double fitnessFunction(PetriObjModel model);

    public PetriObjModel evolve() throws CloneNotSupportedException {

        if (!verbose) {
            System.out.println("No verbose");
        }

        comparator = (left, right) -> {
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

            if (verbose) {
                System.out.format("Generation: %d.\n", i);
            }

            ArrayList<PetriObjModel> crossoverIndividuums = null;
            if (crossoverNumber > 0) {
                crossoverIndividuums = crossover();
            }

            ArrayList<PetriObjModel> mutatedIndividuums = null;
            if (mutationNumber > 0) {
                mutatedIndividuums = mutation();
            }

            ArrayList<PetriObjModel> eliteIndividuums = null;
            if (elitismNumber > 0) {
                eliteIndividuums = elitism();
            }

            population = new ArrayList<>();
            if (eliteIndividuums != null) {
                population.addAll(eliteIndividuums);
            }
            if (mutatedIndividuums != null) {
                population.addAll(mutatedIndividuums);
            }
            if (crossoverIndividuums != null) {
                population.addAll(crossoverIndividuums);
            }

        }

        return population.get(0);
    }

    void setMutableProperties(List<MutableProperty> properties) {
        properties.forEach(initialModel::addMutableProperty);
    }
}
