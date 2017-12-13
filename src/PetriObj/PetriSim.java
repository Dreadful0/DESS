package PetriObj;

import EvolutionaryAlgorithmOptimization.Mutable;
import EvolutionaryAlgorithmOptimization.MutableHolder;
import EvolutionaryAlgorithmOptimization.MutableProperty;
import utils.OptimizationUtils;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class is Petri simulator. <br>
 * The object of this class recreates dynamics of functioning according to Petri
 * net, given in his data field. Such object is named Petri-object.
 *
 * @author Стеценко Інна
 */
public class PetriSim implements Serializable, MutableHolder, Cloneable {

    /**
     * Index of priority, used for mutation
     */
    public static final int PRIORITY = 0;

    private static double timeCurr = 0;
    private static double timeMod = Double.MAX_VALUE - 1;
    private static int next = 1;
    private String name;
    private int numObj;
    private int priority;
    private double timeMin;

    private int numP;
    private int numT;
    private int numIn;
    private int numOut;
    private PetriP[] listP = new PetriP[numP];
    private PetriT[] listT = new PetriT[numT];
    private ArcIn[] listIn = new ArcIn[numIn];
    private ArcOut[] listOut = new ArcOut[numOut];
    private PetriT eventMin;
    private PetriNet net;
    private PetriNet initialNet = net;
    private ArrayList<PetriP> listPositionsForStatistics = new ArrayList<>();

    /**
     * Constructs the Petri simulator with given Petri net and time modeling
     *
     * @param pNet Petri net that describes the dynamics of object
     */
    public PetriSim(PetriNet pNet) {
        net = pNet;
        name = net.getName();
        numObj = next;
        next++;
        timeMin = Double.MAX_VALUE;

        listP = net.getListP();
        listT = net.getListT();
        listIn = net.getArcIn();
        listOut = net.getArcOut();
        numP = listP.length;
        numT = listT.length;
        numIn = listIn.length;
        numOut = listOut.length;
        eventMin = this.getEventMin();
        priority = 0;
        listPositionsForStatistics.addAll(Arrays.asList(listP));

    }

    /**
     * @return the timeCurr
     */
    public static double getTimeCurr() {
        return timeCurr;
    }

    /**
     * @param aTimeCurr the timeCurr to set
     */
    public static void setTimeCurr(double aTimeCurr) {
        timeCurr = aTimeCurr;
    }

    /**
     * @return the timeMod
     */
    private static double getTimeMod() {
        return timeMod;
    }

    /**
     * @param aTimeMod the timeMod to set
     */
    public static void setTimeMod(double aTimeMod) {
        timeMod = aTimeMod;
    }

    static Comparator<PetriSim> getComparatorByPriority() {
        return (o1, o2) -> {
            if (o1.getPriority() < o2.getPriority()) {
                return 1;
            } else if (o1.getPriority() == o2.getPriority()) {
                return 0;
            } else {
                return -1;
            }
        };
    }

    @Override
    public PetriSim clone() throws CloneNotSupportedException { //added 29.11.2017 by Inna

        super.clone();

        return new PetriSim(this.getNet().clone());

    }

    /**
     * @return PetriNet
     */
    public PetriNet getNet() {
        return net;
    }

    /**
     * @return name of Petri-object
     */
    public String getName() {
        return name;
    }

    /**
     * @return list of places for statistics which use for statistics
     */
    ArrayList<PetriP> getListPositionsForStatistics() {
        return listPositionsForStatistics;
    }

    /**
     * Get priority of Petri-object
     *
     * @return value of priority
     */
    int getPriority() {
        return priority;
    }

    /**
     * Set priority of Petri-object
     *
     * @param a value of priority
     */
    public void setPriority(int a) {
        priority = a;
    }

    /**
     * @return the number of object
     */
    int getNumObj() {
        return numObj;
    }

    /**
     * This method uses for describing other actions associated with transition
     * markers output.<br>
     * Such as the output markers into the other Petri-object.<br>
     * The method is overridden in subclass.
     */
    void doT() {

    }

    /**
     * Determines the next event and its moment.
     */
    private void eventMin() {
        PetriT event = null; //пошук часу найближчої події
        // якщо усі переходи порожні, то це означає зупинку імітації,
        // отже за null значенням eventMin можна відслідковувати зупинку імітації
        double min = Double.MAX_VALUE;
        for (PetriT transition : listT) {
            if (transition.getMinTime() < min) {
                event = transition;
                min = transition.getMinTime();
            }
        }
        timeMin = min;
        eventMin = event;
    }

    /**
     * @return moment of next event
     */
    double getTimeMin() {
        return timeMin;
    }

    /**
     * Finds the set of transitions for which the firing condition is true and
     * sorts it on priority value
     *
     * @return the sorted list of transitions with the true firing condition
     */
    private ArrayList<PetriT> findActiveT() {
        ArrayList<PetriT> transitions = new ArrayList<>();

        for (PetriT transition : listT) {
            if ((transition.condition(listP)) && (transition.getProbability() != 0)) {
                transitions.add(transition);
            }
        }

        if (transitions.size() > 1) {
            transitions.sort((o1, o2) -> {
                if (o1.getPriority() < o2.getPriority()) {
                    return 1;
                } else if (o1.getPriority() == o2.getPriority()) {
                    return 0;
                } else {
                    return -1;
                }
            });
        }
        return transitions;
    }

    /**
     * It does one step of simulation: transitions input markers, then finding next event moment, and then transitions input markers
     */
    private void step() { //один крок ,використовується для одного об'єкту мережа Петрі

        System.out.println("Next Step  " + "time=" + getTimeCurr());

        this.printMark();//друкувати поточне маркування
        ArrayList<PetriT> activeTransitions = this.findActiveT();     //формування списку активних переходів

        if ((activeTransitions.isEmpty() && isBufferEmpty()) || getTimeCurr() >= getTimeMod()) { //зупинка імітації за умови, що
            //немає переходів, які запускаються,
            // і немає маркерів у переходах, або вичерпаний час моделювання
            System.out.println("STOP in Net  " + this.getName());
            timeMin = getTimeMod();
            for (PetriP p : listP) {
                p.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            for (PetriT transition : listT) {
                transition.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            setTimeCurr(timeMin);         //просування часу
        } else {
            while (activeTransitions.size() > 0) { //вхід маркерів в переходи доки можливо

                this.solveConflicts(activeTransitions).actIn(listP, getTimeCurr()); //розв'язання конфліктів
                activeTransitions = this.findActiveT(); //оновлення списку активних переходів

            }

            this.eventMin();//знайти найближчу подію та ії час

            for (PetriP position : listP) {
                position.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            for (PetriT transition : listT) {
                transition.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            setTimeCurr(timeMin);         //просування часу
            output();

        }
    }

    /**
     * один крок,використовується для одного об'єкту мережа Петрі(наприклад, покрокова імітація мережі Петрі в графічному редакторі)
     */
    public void step(JTextArea area) {
        area.append("\n Next event, current time = " + getTimeCurr());

        this.printMark();//друкувати поточне маркування
        ArrayList<PetriT> activeT = this.findActiveT();     //формування списку активних переходів
        for (PetriT T : activeT) {
            area.append("\nList of transitions with a fulfilled activation condition " + T.getName());
        }
        if ((activeT.isEmpty() && isBufferEmpty()) || getTimeCurr() >= getTimeMod()) {
            //зупинка імітації за умови, що
            //не має переходів, які запускаються,
            // і не має фішок в переходах або вичерпаний час моделювання
            area.append("\n STOP, there are no active transitions / transitions with a fulfilled activation condition " + this.getName());
            timeMin = getTimeMod();
            for (PetriP position : listP) {
                position.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            for (PetriT transition : listT) {
                transition.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            setTimeCurr(timeMin);         //просування часу
        } else {

            while (activeT.size() > 0) {      //вхід маркерів в переходи доки можливо

                area.append("\n Choosing a transition to activate " + this.solveConflicts(activeT).getName());
                this.solveConflicts(activeT).actIn(listP, getTimeCurr()); //розв'язання конфліктів
                activeT = this.findActiveT(); //оновлення списку активних переходів
            }
            area.append("\n Markers enter transitions:");
            this.printMark(area);//друкувати поточне маркування

            this.eventMin();//знайти найближчу подію та ії час
            for (PetriP position : listP) {
                position.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            for (PetriT transition : listT) {
                transition.changeMean((timeMin - getTimeCurr()) / getTimeMod());
            }

            setTimeCurr(timeMin);         //просування часу

            if (getTimeCurr() <= getTimeMod()) {

                area.append("\n current time =" + getTimeCurr() + "   " + eventMin.getName());
                //Вихід маркерів
                eventMin.actOut(listP);//Вихід маркерів з переходу, що відповідає найближчому моменту часу
                area.append("\n Markers leave a transition " + eventMin.getName());
                this.printMark(area);//друкувати поточне маркування

                if (eventMin.getBuffer() > 0) {

                    boolean u = true;
                    while (u) {
                        eventMin.minEvent();
                        if (eventMin.getMinTime() == getTimeCurr()) {
                            eventMin.actOut(listP);
                        } else {
                            u = false;
                        }
                    }
                    area.append("\n Markers leave a transition buffer " + eventMin.getName());
                    this.printMark(area);//друкувати поточне маркування
                }
                for (PetriT transition : listT) { //ВАЖЛИВО!!Вихід з усіх переходів, що час виходу маркерів == поточний момент час.
                    if (transition.getBuffer() > 0 && transition.getMinTime() == getTimeCurr()) {
                        transition.actOut(listP);//Вихід маркерів з переходу, що відповідає найближчому моменту часу
                        area.append("\n Markers leave a transition " + transition.getName());
                        this.printMark(area);//друкувати поточне маркування
                        if (transition.getBuffer() > 0) {
                            boolean u = true;
                            while (u) {
                                transition.minEvent();
                                if (transition.getMinTime() == getTimeCurr()) {
                                    transition.actOut(listP);
                                } else {
                                    u = false;
                                }
                            }
                            area.append("\n Markers leave a transition buffer " + transition.getName());
                            this.printMark(area);//друкувати поточне маркування
                        }
                    }
                }
            }
        }

    }

    /**
     * It does the transitions input markers
     */
    void input() {//вхід маркерів в переходи Петрі-об'єкта

        ArrayList<PetriT> activeT = this.findActiveT();     //формування списку активних переходів

        if (activeT.isEmpty() && isBufferEmpty()) { //зупинка імітації за умови, що
            //не має переходів, які запускаються,
            timeMin = Double.MAX_VALUE;
        } else {
            while (activeT.size() > 0) { //запуск переходів доки можливо
                this.solveConflicts(activeT).actIn(listP, getTimeCurr()); //розв'язання конфліктів
                activeT = this.findActiveT(); //оновлення списку активних переходів
            }

            this.eventMin();//знайти найближчу подію та ії час
        }
    }

    /**
     * It does the transitions output markers
     */

    private void output() {
        if (getTimeCurr() <= getTimeMod()) {
            eventMin.actOut(listP);//здійснення події
            if (eventMin.getBuffer() > 0) {
                boolean u = true;
                while (u) {
                    eventMin.minEvent();
                    if (eventMin.getMinTime() == getTimeCurr()) {
                        eventMin.actOut(listP);
                    } else {
                        u = false;
                    }
                }
            }
            for (PetriT transition : listT) { //ВАЖЛИВО!!Вихід з усіх переходів, що час виходу маркерів == поточний момент час.

                if (transition.getBuffer() > 0 && transition.getMinTime() == getTimeCurr()) {
                    transition.actOut(listP);//Вихід маркерів з переходу, що відповідає найближчому моменту часу
                    if (transition.getBuffer() > 0) {
                        boolean u = true;
                        while (u) {
                            transition.minEvent();
                            if (transition.getMinTime() == getTimeCurr()) {
                                transition.actOut(listP);
                            } else {
                                u = false;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * It does one event in current moment: the transitions output and input  markers
     */
    void stepEvent() {  //один крок,вихід та вхід маркерів в переходи Петрі-об"єкта, використовується для множини Петрі-об'єктів
        if (isStop()) {
            timeMin = Double.MAX_VALUE;

            return; //зупинка імітації
        }
        output();
        input();
    }

    /**
     * Calculates mean value of quantity of markers in places and quantity of
     * active channels of transitions
     */
    public void doStatistics() {
        for (PetriP position : listP) {
            position.changeMean((timeMin - getTimeCurr()) / getTimeMod());
        }
        for (PetriT transition : listT) {
            transition.changeMean((timeMin - getTimeCurr()) / getTimeMod());
        }

    }

    /**
     * @param dt - the time interval
     */
    void doStatistics(double dt) {
        if (dt > 0) {
            for (PetriP position : listPositionsForStatistics) {
                position.changeMean(dt);
            }
        }
        if (dt > 0) {
            for (PetriT transition : listT) {
                transition.changeMean(dt);
            }
        }
    }

    /**
     * This method use for simulating Petri-object
     */
    public void go() {
        setTimeCurr(0);

        while (getTimeCurr() <= getTimeMod() && !isStop()) {
            PetriSim.this.step();
            if (isStop()) {
                System.out.println("STOP in net  " + this.getName());
            }
            this.printMark();//друкувати поточне маркування
        }
    }

    /**
     * This method use for simulating Petri-object until current time less then
     * the momemt in parametr time
     *
     * @param time - the simulation time
     */
    public void go(double time) {

        while (getTimeCurr() < time && !isStop()) {
            step();
            if (isStop()) {
                System.out.println("STOP in net  " + this.getName());
            }
        }
    }

    public void go(double time, JTextArea area) {

        while (getTimeCurr() < time && !isStop()) {
            step(area);
            if (isStop()) {
                area.append("STOP in net  " + this.getName());
            }
        }
    }

    /**
     * Determines is all of transitions has empty buffer
     *
     * @return true if buffer is empty for all transitions of Petri net
     */
    private boolean isBufferEmpty() {
        boolean c = true;
        for (PetriT e : listT) {
            if (e.getBuffer() > 0) {
                c = false;
                break;
            }
        }
        return c;
    }

    /**
     * Do printing the current marking of Petri net
     */
    void printMark() {
        System.out.print("Mark in Net  " + this.getName() + "   ");
        for (PetriP position : listP) {
            System.out.print(position.getMark() + "  ");
        }
        System.out.println();
    }

    public void printBuffer() {
        System.out.print("Buffer in Net  " + this.getName() + "   ");
        for (PetriT transition : listT) {
            System.out.print(transition.getBuffer() + "  ");
        }
        System.out.println();
    }

    public void printMark(JTextArea area) {
        area.append("\n Mark in Net  " + this.getName() + "   \n");
        for (PetriP position : listP) {
            area.append(position.getMark() + "  ");
        }
        area.append("\n");
    }

    /**
     * @return the nearest event
     */
    final PetriT getEventMin() {
        this.eventMin();
        return eventMin;
    }

    /**
     * This method solves conflict between transitions given in parametr transitions
     *
     * @param transitions the list of transitions
     * @return the transition - winner of conflict
     */
    private PetriT solveConflicts(ArrayList<PetriT> transitions) {
        PetriT transition = transitions.get(0);
        if (transitions.size() > 1) {
            transition = transitions.get(0);
            int i = 0;
            while (i < transitions.size() && transitions.get(i).getPriority() == transition.getPriority()) {
                i++;
            }
            if (i == 1)
                ;
            else {
                double r = Math.random();
                int j = 0;
                double sum = 0;
                double prob;
                while (j < transitions.size() && transitions.get(j).getPriority() == transition.getPriority()) {

                    if (transitions.get(j).getProbability() == 1.0) {
                        prob = 1.0 / i;
                    } else {
                        prob = transitions.get(j).getProbability();
                    }

                    sum = sum + prob;
                    if (r < sum) {
                        transition = transitions.get(j);
                        break;
                    } //вибір переходу за значенням ймовірності
                    else {
                        j++;
                    }
                }
            }
        }

        return transition;
    }

    /**
     * @return the stop
     */
    private boolean isStop() {
        this.eventMin();
        return (eventMin == null);
    }

    public boolean contains(MutableHolder mutableProperty) {
        if (mutableProperty instanceof ArcIn) return containsArcIn((ArcIn) mutableProperty);
        if (mutableProperty instanceof ArcOut) return containsArcOut((ArcOut) mutableProperty);
        if (mutableProperty instanceof PetriP) return containsPlace((PetriP) mutableProperty);
        if (mutableProperty instanceof PetriT) return containsTransition((PetriT) mutableProperty);

        return false;
    }

    private boolean containsArcIn(ArcIn arcIn) {
        for (ArcIn arc : listIn) {
            if (arc.equals(arcIn)) return true;
        }
        return false;
    }

    private boolean containsArcOut(ArcOut arcOut) {
        for (ArcOut arc : listOut) {
            if (arc.equals(arcOut)) return true;
        }
        return false;
    }

    private boolean containsPlace(PetriP place) {
        for (PetriP p : listP) {
            if (p.equals(place)) return true;
        }
        return false;
    }

    private boolean containsTransition(PetriT transition) {
        for (PetriT t : listT) {
            if (t.equals(transition)) return true;
        }
        return false;
    }

    ArcIn getArcIn(int number) {
        for (ArcIn arcIn : listIn) {
            if (arcIn.getNumber() == number) return arcIn;
        }
        return null;
    }

    ArcOut getArcOut(int number) {
        for (ArcOut arcOut : listOut) {
            if (arcOut.getNumber() == number) return arcOut;
        }
        return null;
    }

    PetriP getPlace(int number) {
        for (PetriP place : listP) {
            if (place.getNumber() == number) return place;
        }
        return null;
    }

    PetriT getTransition(int number) {
        for (PetriT t : listT) {
            if (t.getNumber() == number) return t;
        }
        return null;
    }

    @Override
    public void mutate(int property, double mutationRange) {
        if (property == PRIORITY) {
            do {
                priority = OptimizationUtils.mutateInt(priority, mutationRange);
            } while (priority < 0);
        }
    }
}
