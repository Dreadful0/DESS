package PetriObj;

import EvolutionaryAlgorithmOptimization.Mutable;
import EvolutionaryAlgorithmOptimization.MutableProperty;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class provides constructing Petri objective model.<br>
 * List of Petri-objects contains Petri-objects with links between them.<br>
 * For creating Petri-object use class PetriSim. For linking Petri-objects use
 * combining places and passing tokens.<br>
 * Method DoT() of class PetriSim provides programming the passing tokens from
 * the transition of one Petri-object to the place of other.
 *
 * @author Инна
 */
public class PetriObjModel implements Serializable, Cloneable, Mutable {

    private ArrayList<PetriSim> listObj = new ArrayList<>();
    private PetriObjModel initialState;
    private double t;
    private boolean isProtocolPrint = true;
    private boolean shouldGetStatistics = true;
    private ArrayList<LinkByPlaces> links; //added 29.11.2017 by Inna
    private List<MutableProperty> mutableProperties;

    /**
     * Constructs model with given list of Petri objects
     *
     * @param List list of Petri objects
     */
    public PetriObjModel(ArrayList<PetriSim> List) {
        listObj = List;
        links = new ArrayList<>(); //added 29.11.2017 by Inna
        mutableProperties = new ArrayList<>();
    }

    @Override
    public PetriObjModel clone() throws CloneNotSupportedException {  //added 29.11.2017 by Inna
//        super.clone();

        if (initialState == null) {
            ArrayList<PetriSim> copyList = new ArrayList<>();

            for (PetriSim sim : this.listObj) {
                copyList.add(sim.clone());  //  error: we must reproduce shared places
            }
            PetriObjModel clone = new PetriObjModel(copyList);
            //  reproduce combine places

            for (LinkByPlaces li : links) {
                int one = this.getNumInList(li.getOne());
                int other = this.getNumInList(li.getOther());

                if (one >= 0 && other >= 0) {
                    PetriSim oneClone = clone.getListObj().get(one);
                    PetriSim otherClone = clone.getListObj().get(other);
                    clone.linkObjectsCombiningPlaces(oneClone, li.getNumPlaceOne(),
                            otherClone, li.getNumPlaceOther());
                }
            }

            cloneMutableProperties(clone);
            clone.setIsProtoсol(isProtocolPrint);
            clone.setShouldGetStatistics(shouldGetStatistics);
            return clone;
        } else {
            ArrayList<PetriSim> copyList = new ArrayList<>();

            for (PetriSim sim : initialState.listObj) {
                copyList.add(sim.clone());  //  error: we must reproduce shared places
            }
            PetriObjModel clone = new PetriObjModel(copyList);
            //  reproduce combine places

            for (LinkByPlaces li : initialState.links) {
                int one = initialState.getNumInList(li.getOne());
                int other = initialState.getNumInList(li.getOther());

                if (one >= 0 && other >= 0) {
                    PetriSim oneClone = clone.getListObj().get(one);
                    PetriSim otherClone = clone.getListObj().get(other);
                    clone.linkObjectsCombiningPlaces(oneClone, li.getNumPlaceOne(),
                            otherClone, li.getNumPlaceOther());
                }
            }

            cloneMutableProperties(clone);
            clone.setIsProtoсol(initialState.isProtocolPrint);
            clone.setShouldGetStatistics(initialState.shouldGetStatistics);
            return clone;
        }
    }

    private void cloneMutableProperties(PetriObjModel clone) {
        mutableProperties.forEach(property -> {
            if (property.mutableProperty instanceof PetriSim) {
                PetriSim mutable = (PetriSim) property.mutableProperty;
                PetriSim sim = clone.getListObj().get(mutable.getNumObj());
                clone.mutableProperties.add(new MutableProperty(sim, property.propertyKey, property.mutationRange));

            } else if (property.mutableProperty instanceof ArcIn) {
                ArcIn arcIn = (ArcIn) property.mutableProperty;
                for (PetriSim sim : getListObj()) {
                    if (sim.contains(property.mutableProperty)) {
                        int simIndex = getListObj().indexOf(sim);
                        ArcIn in = clone.getListObj().get(simIndex).getArcIn(arcIn.getNumber());
                        clone.mutableProperties.add(
                                new MutableProperty(in, property.propertyKey, property.mutationRange));
                        return;
                    }
                }
            } else if (property.mutableProperty instanceof ArcOut) {
                ArcOut arcIn = (ArcOut) property.mutableProperty;
                for (PetriSim sim : getListObj()) {
                    if (sim.contains(property.mutableProperty)) {
                        int simIndex = getListObj().indexOf(sim);

                        ArcOut out = clone.getListObj().get(simIndex).getArcOut(arcIn.getNumber());
                        clone.mutableProperties.add(
                                new MutableProperty(out, property.propertyKey, property.mutationRange));
                        return;
                    }
                }
            } else if (property.mutableProperty instanceof PetriP) {
                PetriP place = (PetriP) property.mutableProperty;
                for (PetriSim sim : getListObj()) {
                    if (sim.contains(property.mutableProperty)) {
                        int simIndex = getListObj().indexOf(sim);

                        PetriP p = clone.getListObj().get(simIndex).getPlace(place.getNumber());
                        clone.mutableProperties.add(
                                new MutableProperty(p, property.propertyKey, property.mutationRange));
                        return;
                    }
                }
            } else if (property.mutableProperty instanceof PetriT) {
                PetriT transition = (PetriT) property.mutableProperty;
                for (PetriSim sim : getListObj()) {
                    if (sim.contains(property.mutableProperty)) {
                        int simIndex = getListObj().indexOf(sim);

                        PetriT t = clone.getListObj().get(simIndex).getTransition(transition.getNumber());
                        clone.mutableProperties.add(
                                new MutableProperty(t, property.propertyKey, property.mutationRange));
                        return;
                    }
                }
            }
        });
    }

    public int getNumInList(PetriSim sim) {
        int num = -1;
        for (int j = 0; j < listObj.size(); j++) {
            if (sim == listObj.get(j)) {
                num = j;
                break;
            }
        }
        if (num < 0) System.out.println("No such PetriSim " + sim.getName() + " in model's list of objects.");

        return num;
    }

    public void linkObjectsCombiningPlaces(PetriSim one, int numberOne, PetriSim other, int numberOther) { //added 29.11.2017 by Inna

        if (listObj.contains(one) && listObj.contains(other)) {
            one.getNet().getListP()[numberOne] = other.getNet().getListP()[numberOther];   // combine places
            links.add(new LinkByPlaces(one, numberOne, other, numberOther));
        } else {
            System.out.println("ERROR: no such PetriSim objects in model's list of objects");
        }
    }

    public void clearLinks() { //added 29.11.2017 by Inna
        links.clear();
    }

    public void printLinks() { //added 29.11.2017 by Inna
        System.out.println(" number of links " + links.size());
        for (LinkByPlaces li : links) {
            System.out.println(li.getOne().getName() + ".p[" + li.getNumPlaceOne() + "] -> " +
                    li.getOther().getName() + ".p[" + li.getNumPlaceOther() + "] ");
        }
    }

    /**
     * Set need in protocol
     *
     * @param b is true if protocol is needed
     */
    public void setIsProtoсol(boolean b) {
        isProtocolPrint = b;
    }

    /**
     * Set need in statistics
     *
     * @param shouldGetStatistics is true if should get statistics, false otherwise
     */
    public void setShouldGetStatistics(boolean shouldGetStatistics) {
        this.shouldGetStatistics = shouldGetStatistics;
    }

    /**
     * @return the list of Petri objects of model
     */
    public ArrayList<PetriSim> getListObj() {
        return listObj;
    }

    /**
     * Simulating from zero time until the time equal time modeling
     *
     * @param timeModeling time modeling
     */
    public void go(double timeModeling) throws CloneNotSupportedException {
        if (initialState == null) initialState = this.clone();

        PetriSim.setTimeMod(timeModeling);
        PetriSim.setTimeCurr(0);

        t = 0;
        double min;
        listObj.sort(PetriSim.getComparatorByPriority());

        for (PetriSim e : listObj) {
            e.input();
        }

        if (isProtocolPrint) {
            for (PetriSim e : listObj) {
                e.printMark();
            }
        }
        ArrayList<PetriSim> conflictObj = new ArrayList<>();
        Random r = new Random();

        while (t < timeModeling) {
            conflictObj.clear();

            min = listObj.get(0).getTimeMin();  //пошук найближчої події

            for (PetriSim e : listObj) {
                if (e.getTimeMin() < min) {
                    min = e.getTimeMin();
                }
            }

            if (shouldGetStatistics) {
                for (PetriSim e : listObj) {
                    e.doStatistics((min - t) / min); //статистика за час "дельта т", для спільних позицій потрібно статистику збирати тільки один раз!!!

                }
            }

            t = min; // просування часу

            PetriSim.setTimeCurr(t); // просування часу //3.12.2015

            if (isProtocolPrint) {
                System.out.println(" Time progress: time = " + t + "\n");
            }
            if (t <= timeModeling) {

                for (PetriSim e : listObj) {
                    if (t == e.getTimeMin()) // розв'язання конфлікту об'єктів рівноймовірнісним способом
                    {
                        conflictObj.add(e);    // список конфліктних обєктів
                    }
                }
                int num;
                int max;
                if (isProtocolPrint) {
                    System.out.println(" List of conflicting objects  " + "\n");
                    for (int ii = 0; ii < conflictObj.size(); ii++) {
                        System.out.println(" K [ " + ii + "  ] = " + conflictObj.get(ii).getName() + "\n");
                    }
                }

                if (conflictObj.size() > 1) { //вибір обєкта, що запускається
                    max = conflictObj.size();
                    System.out.println(max);

                    conflictObj.sort(PetriSim.getComparatorByPriority());

                    for (int i = 1; i < conflictObj.size(); i++) {
                        if (conflictObj.get(i).getPriority() < conflictObj.get(i - 1).getPriority()) {
                            max = i - 1;
                            break;
                        }

                    }
                    if (max == 0) {
                        num = 0;
                    } else {
                        num = r.nextInt(max);
                    }
                } else {
                    num = 0;
                }

                if (isProtocolPrint) {
                    System.out.println(" Selected object  " + conflictObj.get(num).getName() + "\n" + " NextEvent " + "\n");
                }

                for (PetriSim e : listObj) {
                    if (e.getNumObj() == conflictObj.get(num).getNumObj()) {
                        if (isProtocolPrint) {
                            System.out.println(" time =   " + t + "Event '" + e.getEventMin().getName() + "'\n"
                                    + "is occuring for the object   " + e.getName() + "\n");
                        }
                        e.doT();
                        e.stepEvent();

                    }

                }
                if (isProtocolPrint) {
                    System.out.println("Markers leave transitions:");
                    for (PetriSim e : listObj) //ДРУК поточного маркірування
                    {
                        e.printMark();
                    }
                }
                listObj.sort(PetriSim.getComparatorByPriority());
                for (PetriSim e : listObj) {
                    //можливо змінились умови для інших обєктів
                    e.input(); //вхід маркерів в переходи Петрі-об'єкта

                }
                if (isProtocolPrint) {
                    System.out.println("Markers enter transitions:");
                    for (PetriSim e : listObj) { //ДРУК поточного маркірування
                        e.printMark();
                    }
                }
            }
        }
    }

    /**
     * Prints the string in given JTextArea object
     *
     * @param info string for printing
     * @param area specifies where simulation protokol is printed
     */
    private void printInfo(String info, JTextArea area) {
        if (isProtocolPrint)
            area.append(info);
    }

    /**
     * Prints the quantity for each position of Petri net
     *
     * @param area specifies where simulation protokol is printed
     */
    private void printMark(JTextArea area) {
        if (isProtocolPrint) {
            for (PetriSim e : listObj) {
                e.printMark(area);
            }
        }
    }

    public void go(double timeModeling, JTextArea area) { //виведення протоколу подій та результатів моделювання у об"єкт класу JTextArea
        area.setText(" Events protocol ");
        PetriSim.setTimeMod(timeModeling);
        t = 0;
        double min;
        listObj.sort(PetriSim.getComparatorByPriority());
        for (PetriSim e : listObj) {
            e.input();
        }
        this.printMark(area);
        ArrayList<PetriSim> conflictObj = new ArrayList<>();
        Random r = new Random();

        while (t < timeModeling) {

            conflictObj.clear();

            min = Double.MAX_VALUE;  //пошук найближчої події

            for (PetriSim e : listObj) {
                if (e.getTimeMin() < min) {
                    min = e.getTimeMin();
                }
            }
            if (shouldGetStatistics) {
                for (PetriSim e : listObj) {
                    if (min > 0) {
                        e.doStatistics((min - t) / min); //статистика за час "дельта т", для спільних позицій потрібно статистику збирати тільки один раз!!!
                    }
                }
            }

            t = min; // просування часу

            PetriSim.setTimeCurr(t); // просування часу


            this.printInfo(" \n Time progress: time = " + t + "\n", area);

            if (t <= timeModeling) {

                for (PetriSim e : listObj) {
                    if (t == e.getTimeMin()) { // розв'язання конфлікту об'єктів рівноймовірнісним способом

                        conflictObj.add(e);      // список конфліктних обєктів
                    }
                }
                int num;
                int max;
                if (isProtocolPrint) {
                    area.append("  List of conflicting objects " + "\n");
                    for (int ii = 0; ii < conflictObj.size(); ii++) {
                        area.append("  K [ " + ii + "  ] = " + conflictObj.get(ii).getName() + "\n");
                    }
                }

                if (conflictObj.size() > 1) // вибір обєкта, що запускається
                {
                    max = conflictObj.size();
                    listObj.sort(PetriSim.getComparatorByPriority());
                    for (int i = 1; i < conflictObj.size(); i++) {
                        if (conflictObj.get(i).getPriority() < conflictObj.get(i - 1).getPriority()) {
                            max = i - 1;

                            break;
                        }

                    }
                    if (max == 0) {
                        num = 0;
                    } else {
                        num = r.nextInt(max);
                    }
                } else {
                    num = 0;
                }


                this.printInfo(" Selected object  " + conflictObj.get(num).getName() + "\n" + " NextEvent " + "\n", area);


                for (PetriSim list : listObj) {
                    if (list.getNumObj() == conflictObj.get(num).getNumObj()) {
                        this.printInfo(" time = " + t + "   Event '" + list.getEventMin().getName() +
                                "'\n" + "is occuring for the object " + list.getName() + "\n", area);
                        list.doT();
                        list.stepEvent();
                    }
                }
                this.printInfo("Markers leave transitions:", area);
                this.printMark(area);
                listObj.sort(PetriSim.getComparatorByPriority());
                for (PetriSim e : listObj) {
                    //можливо змінились умови для інших обєктів
                    e.input(); //вхід маркерів в переходи Петрі-об'єкта

                }

                this.printInfo("Markers enter transitions:", area);
                this.printMark(area);
            }
        }
        area.append("\n Modeling results: \n");

        for (PetriSim e : listObj) {
            area.append("\n Petri-object " + e.getName());
            area.append("\n Mean values of the quantity of markers in places : ");
            for (PetriP P : e.getListPositionsForStatistics()) {
                area.append("\n  Place '" + P.getName() + "'  " + Double.toString(P.getMean()));
            }
            area.append("\n Mean values of the quantity of active transition channels : ");
            for (PetriT T : e.getNet().getListT()) {
                area.append("\n Transition '" + T.getName() + "'  " + Double.toString(T.getMean()));
            }
        }
    }

    @Override
    public void mutate() throws CloneNotSupportedException {
        for (MutableProperty mutableProperty : mutableProperties) {
            mutableProperty.mutableProperty.mutate(mutableProperty.propertyKey, mutableProperty.mutationRange);
        }
        if (initialState == null) initialState = this.clone();
//        initialState.getListObj().forEach(petriSim -> petriSim.mutate(mutableRange));
    }

    public void addMutableProperty(MutableProperty property) {
        mutableProperties.add(property);
    }

    public void setMutableProperties(List<MutableProperty> mutableProperties) {
        this.mutableProperties = mutableProperties;
    }

    private class LinkByPlaces { //added 29.11.2017 by Inna
        PetriSim one, other;
        int numOne, numOther;

        LinkByPlaces(PetriSim simOne, int nOne, PetriSim simOther, int nOther) {
            one = simOne;
            other = simOther;
            numOne = nOne;
            numOther = nOther;

        }

        private PetriSim getOne() {
            return one;
        }

        private PetriSim getOther() {
            return other;
        }

        private int getNumPlaceOne() {
            return numOne;
        }

        private int getNumPlaceOther() {
            return numOther;
        }

    }

}
