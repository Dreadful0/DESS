package PetriObj;

import EvolutionaryAlgorithmOptimization.Mutable;
import utils.OptimizationUtils;

import java.io.Serializable;

/**
 * This class for creating the arc(arc) between transition and place of Petri
 * net (and directed from transion to place)
 *
 * @author Стеценко Інна
 */
public class ArcOut implements Cloneable, Serializable, Mutable {
	
	private int numP;
	private int numT;
	private int k; // for mutation
	private String nameT;
	private String nameP;
	
	// whether k is a parameter; added by Katya 08.12.2016
	private boolean kIsParam = false;
	// param name
	private String kParamName = null;
	
	/**
	 *
	 */
	public ArcOut() {
		k = 1;
	}
	
	/**
	 * @param T number of transition
	 * @param P number of place
	 * @param K arc multiplicity
	 */
	public ArcOut(int T, int P, int K) {
		numP = P;
		numT = T;
		k = K;
	}
	
	/**
	 * @param T number of transition
	 * @param P number of place
	 * @param K arc multiplicity
	 */
	public ArcOut(PetriT T, PetriP P, int K) {
		numP = P.getNumber();
		numT = T.getNumber();
		k = K;
		nameP = P.getName();
		nameT = T.getName();
	}
	
	public boolean kIsParam() {
		return kIsParam;
	}
	
	public String getKParamName() {
		return kParamName;
	}
	
	public void setKParam(String paramName) {
		if (paramName == null) {
			kIsParam = false;
			kParamName = null;
		} else {
			kIsParam = true;
			kParamName = paramName;
			k = 1;
		}
	}
	
	/**
	 * @return arc multiplicity
	 */
	public int getQuantity() {
		return k;
	}
	
	/**
	 * @param K arc multiplicity
	 */
	public void setQuantity(int K) {
		k = K;
	}
	
	/**
	 * @return the number of place that is end of the arc
	 */
	public int getNumP() {
		return numP;
	}
	
	/**
	 * @param n the number of place that is end of the arc
	 */
	public void setNumP(int n) {
		numP = n;
	}
	
	/**
	 * @return number of transition that is beginning of the arc
	 */
	public int getNumT() {
		return numT;
	}
	
	/**
	 * @param n number of transition that is beginning of the arc
	 */
	public void setNumT(int n) {
		numT = n;
	}
	
	/**
	 * @return name of transition that is the beginning of the arc
	 */
	public String getNameT() {
		return nameT;
	}
	
	/**
	 * @param s name of transition that is the beginning of the arc
	 */
	public void setNameT(String s) {
		nameT = s;
	}
	
	/**
	 * @return name of place that is the end of the arc
	 */
	public String getNameP() {
		return nameP;
	}
	
	/**
	 * @param s name of place that is the end of the arc
	 */
	public void setNameP(String s) {
		nameP = s;
	}
	
	/**
	 *
	 */
	void print() {
		if (nameP != null && nameT != null) {
			System.out.println(" T=  " + nameT + ", P=  " + nameP + ", k= " + getQuantity());
		} else {
			System.out.println(" T= T" + numT + ", P= P" + numP + ", k= " + getQuantity());
		}
	}
	
	/**
	 * @return TieOut object with parameters which copy current parameters of
	 * this arc
	 * @throws java.lang.CloneNotSupportedException if Petri net has invalid structure
	 */
	@Override
	public ArcOut clone() throws CloneNotSupportedException {
		super.clone();
		return new ArcOut(numT, numP, k);
		
	}
	
	public void printParameters() {
		System.out.println("This arc has direction from  transition  with number " + numT + " to place with number " + numP
				+ " and has " + k + " value of multiplicity");
	}
	
	@Override
	public void mutate(double mutableRange) {
		k = OptimizationUtils.mutateInt(k, mutableRange);
	}
}
