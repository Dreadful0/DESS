package PetriObj;

import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This class for creating the place of Petri net.
 *
 * @author Стеценко Інна
 */
public class PetriP extends PetriMainElement implements Cloneable, Serializable { // inheritance added by Katya 20.11.2016

    private int mark;
    private String name;
    private int number;
    private double mean; // for fitness
    private static int next = 0;//додано 1.10.2012, лічильник об"єктів
    private int observedMax;
    private int observedMin;

    // whether mark is a parameter; added by Katya 08.12.2016
    private boolean markIsParam = false;
    // param name
    private String markParamName = null;
    
    /**
     *
     * @param n name of place
     * @param m quantity of markers
     */
    public PetriP(String n, int m) {
//        System.out.println("PetriP constructor (name, m)");
        name = n;
//        System.out.println("PetriP name " + name);
        mark = m;
//        System.out.println("PetriP mark " + mark);
        mean = 0;
        number = next; //додано 1.10.2012
//        System.out.println("PetriP number " + number);
        next++;
        observedMax = m;
        observedMin = m;
    }

    /**
     *
     * @param n - the name of place
     */
    public PetriP(String n) {
//        System.out.println("PetriP constructor (name)");
        name = n;
//        System.out.println("PetriP name " + name);
        mark = 0;
        mean = 0;
        number = next; //додано 1.10.2012
//        System.out.println("PetriP number " + number);
        next++;
        observedMax = 0;
        observedMin = 0;
    }

    public boolean markIsParam() {
        return markIsParam;
    }
    
    public String getMarkParamName() {
        return markParamName;
    }
    
    public void setMarkParam(String paramName) {
        if (paramName == null) {
            markIsParam = false;
            markParamName = null;
        } else {
            markIsParam = true;
            markParamName = paramName;
            mark = 0;
        }
    }
    
    /**
     * Set the counter of places to zero.
     */
    public static void initNext(){ //ініціалізація лічильника нульовим значенням
//        System.out.println("PetriP initNext");
        next = 0;
    }

    /**
     * /**
     * Recalculates the mean value
     *
     * @param a value for recalculate of mean value (value equals product of
     * marking and time divided by time modeling)
     */
    public void changeMean(double a) {
        mean = mean + (mark - mean) * a;
//        System.out.println("changeMean " + mean);
    }

    /**
     *
     * @return mean value of quantity of markers
     */
    public double getMean() {
        return mean;
    }

    /**
     *
     * @param a value on which increase the quantity of markers
     */
    public void increaseMark(int a) {
        mark += a;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }

    }

    /**
     *
     * @param a value on which decrease the quantity of markers
     */
    public void decreaseMark(int a) {
        mark -= a;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }
    }

    /**
     *
     * @return current quantity of markers
     */
    public int getMark() {
        return mark;
    }

    public int getObservedMax() {
        return observedMax;
    }

    public int getObservedMin() {
        return observedMin;
    }

    /**
     * Set quantity of markers
     *
     * @param a quantity of markers
     */
    public void setMark(int a) {
        mark = a;
    }

    /**
     *
     * @return name of the place
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param s - the new name of place
     */
    public void setName(String s) {
        name = s;
    }

    /**
     *
     * @return number of the place
     */
    public int getNumber() {
        return number;
    }

    /**
     *
     * @param n - the new number of place
     */
    public void setNumber(int n) {
//        System.out.println("PetriP setNumber");
        number = n;
//        System.out.println("PetriP number " + number);
    }

    /**
     *
     * @return PetriP object with parameters which copy current parameters of
     * this place
     * @throws java.lang.CloneNotSupportedException if Petri net has invalid structure
     */
    @Override
    public PetriP clone() throws CloneNotSupportedException {
//        System.out.println("PetriP clone");
        PetriP P = new PetriP(name, this.getMark()); // 14.11.2012
        P.setNumber(number); //номер зберігається для відтворення зв"язків між копіями позицій та переходів
        return P;
    }

    public void printParameters() {
        System.out.println("Place " + name + "has such parametrs: \n"
                + " number " + number + ", mark " + mark);
    }

}
