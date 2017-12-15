package PetriObj;

import EvolutionaryAlgorithmOptimization.MutableHolder;

import java.io.Serializable;

/**
 * This class for creating the place of Petri net.
 *
 * @author Стеценко Інна
 */
public class PetriP extends PetriMainElement implements Cloneable, Serializable, MutableHolder {

    /**
     * Index of mark property, user for mutation
     */
    public static final int MARK = 0;

    private static int next = 0;
    private int mark;
    private String name;
    private int number;
    private double mean; // for fitness
    private int observedMax;
    private int observedMin;

    // whether mark is a parameter; added by Katya 08.12.2016
    private boolean markIsParam = false;
    // param name
    private String markParamName = null;

    /**
     * @param n name of place
     * @param m quantity of markers
     */
    public PetriP(String n, int m) {
        name = n;
        mark = m;
        mean = 0;
        number = next;
        next++;
        observedMax = m;
        observedMin = m;
    }

    /**
     * @param n - the name of place
     */
    public PetriP(String n) {
        name = n;
        mark = 0;
        mean = 0;
        number = next;
        next++;
        observedMax = 0;
        observedMin = 0;
    }

    /**
     * Set the counter of places to zero.
     */
    public static void initNext() {
        next = 0;
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
     * /**
     * Recalculates the mean value
     *
     * @param a value for recalculate of mean value (value equals product of
     *          marking and time divided by time modeling)
     */
    void changeMean(double a) {
        mean = mean + (mark - mean) * a;
    }

    /**
     * @return mean value of quantity of markers
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param a value on which increase the quantity of markers
     */
    void increaseMark(int a) {
        mark += a;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }

    }

    /**
     * @param a value on which decrease the quantity of markers
     */
    void decreaseMark(int a) {
        mark -= a;
        if (observedMax < mark) {
            observedMax = mark;
        }
        if (observedMin > mark) {
            observedMin = mark;
        }
    }

    /**
     * @return current quantity of markers
     */
    public int getMark() {
        return mark;
    }

    /**
     * Set quantity of markers
     *
     * @param a quantity of markers
     */
    public void setMark(int a) {
        mark = a;
    }

    public int getObservedMax() {
        return observedMax;
    }

    public int getObservedMin() {
        return observedMin;
    }

    /**
     * @return name of the place
     */
    public String getName() {
        return name;
    }

    /**
     * @param s - the new name of place
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * @return number of the place
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param n - the new number of place
     */
    public void setNumber(int n) {
        number = n;
    }

    /**
     * @return PetriP object with parameters which copy current parameters of
     * this place
     * @throws java.lang.CloneNotSupportedException if Petri net has invalid structure
     */
    @Override
    public PetriP clone() throws CloneNotSupportedException {
        super.clone();
        PetriP place = new PetriP(name, this.getMark());
        place.setNumber(number); //номер зберігається для відтворення зв"язків між копіями позицій та переходів
        return place;
    }

    public void printParameters() {
        System.out.println("Place " + name + " has such parametrs: \n"
                + " number " + number + ", mark " + mark);
    }

    @Override
    public void mutate(int property, double mutationRange) {
        // TODO should we add any special probabilities for increasing/decreasing mark?

        if (property == MARK) {
            double changeIndex = Math.random();
            if (changeIndex < 0.5) {
                mark += 1;
            } else {
                mark = (mark - 1 > 0) ? mark - 1 : mark;
            }
        }
    }

    public boolean customEquals(Object obj) {
        return (obj instanceof PetriP &&
                this.mark == ((PetriP) obj).mark &&
                this.name.equals(((PetriP) obj).name) &&
                this.number == ((PetriP) obj).number
//                this.mean == ((PetriP) obj).mean &&
//                this.observedMax == ((PetriP) obj).observedMax &&
//                this.observedMin == ((PetriP) obj).observedMin &&
//                this.markIsParam == ((PetriP) obj).markIsParam &&
//                (this.markParamName == null ||
//                        this.markParamName.equals(((PetriP) obj).markParamName))
        );
    }
}
