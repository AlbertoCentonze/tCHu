package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * @author Emma Poggiolini (330757)
 */

public final class Ticket implements Comparable<Ticket> {

    // list of trips written on the ticket
    private final List<Trip> trips = new ArrayList<Trip>();

    // text on the ticket
    private final String text = null;

    // doesn't specify to put public (?)
    /**
     * primary constructor
     * initializes the attribute trips
     * @param trips
     */
    public Ticket(List<Trip> trips) {
        // checking if list is empty
        Preconditions.checkArgument(trips.isEmpty());
        // TODO check that each departure station has the same name (need class Trip)

        for(Trip trip : trips) {
            this.trips.add(trip);
        }

        // stock visual representation of ticket
        text = computeText();
    }

    /**
     * secondary constructor
     * creates a single-trip ticket
     */
    public Ticket(Station from, Station to, int points) {
        // TODO need class Trip to fill in
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * paste the visual representation of the ticket
     * @return (String) text
     */
    public String text() {return text;}

    /**
     * create String of visual representation of the ticket
     * called in the primary constructor
     * @return (String) visual representation of ticket
     */
    private static String computeText() {
        // name of the departure-station
        String fromStation = trips.get(0).getFrom().toString(); // TODO once class Trip get Station from

        // create TreeSet and add all destination-stations
        TreeSet<String> names = new TreeSet<>();
        for(Trip trip : trips) {
            names.add(trip.getTo().toString() + " (" + trip.getPoints().toString() + ")"); // TODO once class Trip get Station to
        }

        // return String with visual representation of ticket
        // chain name of departure-station to list of destination-stations
        if(trips.size() = 1) {
            return String.format("%s - %s",fromStation,names);
        } else {
            return String.format("%s - {%s}",fromStation,String.join(", ",names));
        }
    }

    /**
     * points attributed once a trip on the ticket is finished
     * @param (StationConnectivity) connectivity
     * @return (int) points
     */
    public int points(StationConnectivity connectivity) {
        if(connectivity.connected()) {
            // TODO finish method
        }
    }

    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

    @Override
    public String toString() {return text();}
}
