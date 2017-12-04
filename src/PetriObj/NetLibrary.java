package PetriObj;

import java.util.ArrayList;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class contains methods for creating Petri net in accordance with choosen
 * object and with given parameters.
 *
 * @author Стеценко Інна
 */
public class NetLibrary {
    public static PetriNet CreateNetUser(double tGen, String distribution) throws ExceptionInvalidNetStructure {
        ArrayList<PetriP> places = new ArrayList<>();
        ArrayList<PetriT> transitions = new ArrayList<>();
        ArrayList<ArcIn> arcsIn = new ArrayList<>();
        ArrayList<ArcOut> arcsOut = new ArrayList<>();
        places.add(new PetriP("User", 1));
        places.add(new PetriP("P2", 0));
        places.add(new PetriP("P3", 0));
        places.add(new PetriP("Alarm", 0));
        places.add(new PetriP("Harmless", 0));
        places.add(new PetriP("Executed", 0));
        places.add(new PetriP("NewPacket", 0));
        transitions.add(new PetriT("SendPack", tGen));
        transitions.get(0).setDistribution(distribution, tGen);
        transitions.add(new PetriT("ControlTime", 10.0));
        transitions.add(new PetriT("T3", 0.0));
        transitions.add(new PetriT("T4", 0.0));
        transitions.get(3).setPriority(2);
        arcsIn.add(new ArcIn(places.get(0), transitions.get(0), 1));
        arcsIn.add(new ArcIn(places.get(1), transitions.get(1), 1));
        arcsIn.add(new ArcIn(places.get(2), transitions.get(2), 1));
        arcsIn.add(new ArcIn(places.get(2), transitions.get(3), 1));
        arcsIn.add(new ArcIn(places.get(5), transitions.get(3), 1));
        arcsOut.add(new ArcOut(transitions.get(0), places.get(0), 1));
        arcsOut.add(new ArcOut(transitions.get(0), places.get(1), 1));
        arcsOut.add(new ArcOut(transitions.get(1), places.get(2), 1));
        arcsOut.add(new ArcOut(transitions.get(2), places.get(3), 1));
        arcsOut.add(new ArcOut(transitions.get(3), places.get(4), 1));
        arcsOut.add(new ArcOut(transitions.get(0), places.get(6), 1));
        PetriNet net = new PetriNet("user", places, transitions, arcsIn, arcsOut);
        PetriP.initNext();
        PetriT.initNext();
        ArcOut.initNext();
        ArcIn.initNext();

        return net;
    }
}
