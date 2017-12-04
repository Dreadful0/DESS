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

//        PetriObjModel model2 = model.clone();
//        model2.go(timeModeling);
//        getResults(model2);
//
//        PetriObjModel model3 = model2.clone();
//        model3.go(timeModeling);
//        getResults(model3);

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
        optimizer.setProbabilities(0.2, 0.8, 0, 0.5);

        PetriObjModel best = optimizer.evolve();

    }

    public static void getResults(PetriObjModel model) {
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }
//        System.out.println("Mean value of channel worked");
//        for (int j = 1; j < 4; j++) {
//            System.out.println(1.0 - model.getListObj().get(j).getNet().getListP()[1].getMean());
//        }
//        System.out.println(2.0 - model.getListObj().get(4).getNet().getListP()[1].getMean());
//
//        System.out.println("Estimation precision");
//        double[] valuesQueue = {1.786, 0.003, 0.004, 0.00001};
//
//        System.out.println(" Mean value of queue  precision: ");
//        for (int j = 1; j < 5; j++) {
//            double inaccuracy = (model.getListObj().get(j).getNet().getListP()[0].getMean() - valuesQueue[j - 1]) / valuesQueue[j - 1] * 100;
//            inaccuracy = Math.abs(inaccuracy);
//            System.out.println(inaccuracy + " %");
//        }
//
//        double[] valuesChannel = {0.714, 0.054, 0.062, 0.036};
//
//        System.out.println(" Mean value of channel worked  precision: ");
//
//        for (int j = 1; j < 4; j++) {
//            double inaccuracy = (1.0 - model.getListObj().get(j).getNet().getListP()[1].getMean() - valuesChannel[j - 1]) / valuesChannel[j - 1] * 100;
//            inaccuracy = Math.abs(inaccuracy);
//
//            System.out.println(inaccuracy + " %");
//        }
//        double inaccuracy = (2.0 - model.getListObj().get(4).getNet().getListP()[1].getMean() - valuesChannel[3]) / valuesChannel[3] * 100;
//        inaccuracy = Math.abs(inaccuracy);
//
//        System.out.println(inaccuracy + " %");
    }
}
