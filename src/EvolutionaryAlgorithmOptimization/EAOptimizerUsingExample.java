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
        model.setIsProtoсol(false);
        model.setShouldGetStatistics(true);

        int timeModeling = 1000000;

        EAOptimizer optimizer = new EAOptimizer(model, timeModeling) {
            @Override
            protected ArrayList<PetriObjModel> crossover() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public double fitnessFunction(PetriObjModel model) {
                // Mean value of queue -> min
                double res = 0;
                for (int j = 1; j < 5; j++) {
                    res += Math.pow(model.getListObj().get(j).getNet().getListP()[0].getMean(), 2);
                }
                return res;
            }
        };

        optimizer.setPopulationSize(5);
        optimizer.setGenerationsNumber(10);
        optimizer.setOptType(OptType.OPT_MIN);
        optimizer.setProbabilities(0.4, 0.6, 0, 0.05);

        PetriObjModel best = optimizer.evolve();

    }

    public static void getResults(PetriObjModel model) {
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }
    }
}
