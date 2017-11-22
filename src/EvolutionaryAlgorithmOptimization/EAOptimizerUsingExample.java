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
 *
 * @author masha
 */
public class EAOptimizerUsingExample {
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure, Exception{
        PetriObjModel model = TestPetriObjPaint.getModel();
        
        EAOptimizer optimizer = new EAOptimizer(model) {
            @Override
            protected ArrayList<PetriObjModel> crossover() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }            

            @Override
            public double fitness_function(PetriObjModel model) {
                return 0;
            }
        };
        optimizer.setPopulationSize(10);
        optimizer.setGenerationsNumber(10);
        optimizer.setProbabilities(0.2, 0.8, 0, 0.75);
        
        PetriObjModel best = optimizer.evolve();
        
    }
}
