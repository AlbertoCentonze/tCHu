package ch.epfl.tchu.game;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class Ticket implements Comparable<Ticket> {

    // list of trips written on the ticket
    private List<Trip> trips = new List<Trip>();

    // doesn't specify to put public (?)
    /**
     * primary constructor
     * initializes the attribute trips
     * @param trips
     */
    public Ticket(List<Trip> trips) {
        for(Trip trip : trips) {
            this.trips.add(trip);
        }
    }

    /**
     * secondary constructor
     */
    public Ticket(Station from, Station to, int points) {
        // need class Trip to fill in
    }

    @Override 
    public int compareTo(Ticket that) {

    }
}
