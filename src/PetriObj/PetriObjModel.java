package PetriObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextArea;

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
public class PetriObjModel implements Serializable, Cloneable {

	private ArrayList<PetriSim> listObj = new ArrayList<>();
	private ArrayList<PetriSim> initialListObj = new ArrayList<>();
	private double t;
	private boolean isProtokolPrint = true;
	private boolean shouldGetStatistics = true;

	/**
	 * Constructs model with given list of Petri objects
	 *
	 * @param List list of Petri objects
	 */
	public PetriObjModel(ArrayList<PetriSim> List) throws CloneNotSupportedException {
		listObj = List;
		for (PetriSim petriSim : listObj) {
			initialListObj.add(petriSim.clone());
		}
	}

	@Override
	public PetriObjModel clone() throws CloneNotSupportedException {
		super.clone();

		PetriObjModel petriObjModel = new PetriObjModel(initialListObj);
		petriObjModel.setIsProtoсol(isProtokolPrint);
		petriObjModel.setShouldGetStatistics(shouldGetStatistics);
		return petriObjModel;
	}

	/**
	 * Set need in protocol
	 *
	 * @param b is true if protocol is needed
	 */
	public void setIsProtoсol(boolean b) {
		isProtokolPrint = b;
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
	public void go(double timeModeling) {
		PetriSim.setTimeMod(timeModeling);

		t = 0;
		double min;
		listObj.sort(PetriSim.getComparatorByPriority());

		for (PetriSim e : listObj) {
			e.input();
		}

		if (isProtokolPrint) {
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

			if (isProtokolPrint) {
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
				if (isProtokolPrint) {
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

				if (isProtokolPrint) {
					System.out.println(" Selected object  " + conflictObj.get(num).getName() + "\n" + " NextEvent " + "\n");
				}

				for (PetriSim e : listObj) {
					if (e.getNumObj() == conflictObj.get(num).getNumObj()) {
						if (isProtokolPrint) {
							System.out.println(" time =   " + t + "Event '" + e.getEventMin().getName() + "'\n"
									+ "is occuring for the object   " + e.getName() + "\n");
						}
						e.doT();
						e.stepEvent();

					}

				}
				if (isProtokolPrint) {
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
				if (isProtokolPrint) {
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
		if (isProtokolPrint)
			area.append(info);
	}

	/**
	 * Prints the quantity for each position of Petri net
	 *
	 * @param area specifies where simulation protokol is printed
	 */
	private void printMark(JTextArea area) {
		if (isProtokolPrint) {
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
				if (isProtokolPrint) {
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

}
