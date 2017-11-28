/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import LibTest.TestPetriObjPaint;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;

import java.util.ArrayList;

/**
 * @author masha
 */
public class EAOptimizerUsingExample {
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure, Exception {
        PetriObjModel model = TestPetriObjPaint.getModel();
        model.setIsProtokol(false);
        int timeModeling = 1000000;

        EAOptimizer optimizer = new EAOptimizer(model, timeModeling) {
            @Override
            protected ArrayList<PetriObjModel> crossover() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public double fitnessFunction(PetriObjModel model) {
                return 0;
            }
        };

        model.go(timeModeling);
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }

        optimizer.setPopulationSize(10);
        optimizer.setGenerationsNumber(10);
        optimizer.setProbabilities(0.2, 0.8, 0, 0.5);

        PetriObjModel best = optimizer.evolve();

    }
}
