/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import LibTest.TestPetriObjPaint;
import PetriObj.ArcIn;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;

/**
 * @author masha
 */
public class EAOptimizerUsingExample {
    public static void main(String[] args) throws Exception {
        PetriObjModel model = TestPetriObjPaint.getModel();
        model.setIsProtoсol(false);
        model.setShouldGetStatistics(true);

        int timeModeling = 1000000;

        EAOptimizer optimizer = new EAOptimizer(model, timeModeling) {

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
        optimizer.setMutableProperties(new MutationBuilder()
                .add(new MutableProperty(model.getListObj().get(1).getNet().getArcIn()[0], ArcIn.K, 0.1))
                .build());
        optimizer.setProbabilities(0.2, 0, 0.2);
        optimizer.setVerbose(true);

        PetriObjModel best = optimizer.evolve();

    }

    public static void getResults(PetriObjModel model) {
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }
    }
}
