/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvolutionaryAlgorithmOptimization;

import LibTest.TestPetriObjPaint;
import PetriObj.ArcIn;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;
import PetriObj.PetriT;


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

        ArcIn mutableArc = model.getListObj().get(1).getNet().getArcIn()[0];
        PetriSim mutableSim = model.getListObj().get(0);
        PetriT mutableT = model.getListObj().get(0).getNet().getListT()[0];
        optimizer.setPopulationSize(5);
        optimizer.setGenerationsNumber(10);
        optimizer.setOptType(OptType.OPT_MIN);
        optimizer.setMutableProperties(new MutationBuilder()
                .add(new MutableProperty(mutableArc, ArcIn.K, 0.1))
                .add(new MutableProperty(mutableSim, PetriSim.PRIORITY, 0.5))
                .add(new MutableProperty(mutableT, PetriT.MIN_TIME, 0.3))
                .build());
        optimizer.setProbabilities(0.3, 0.3, 0.4);
        optimizer.setVerbose(true);

        PetriObjModel best = optimizer.evolve();
        best.printDiff(model);

    }

    public static void getResults(PetriObjModel model) {
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }
    }

}
