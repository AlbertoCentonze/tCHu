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
    private final String text;

    /**
     * primary constructor
     * initializes the attribute trips
     * @param trips
     */
    public Ticket(List<Trip> trips) {
        // checking if list is empty
        Preconditions.checkArgument(!trips.isEmpty());

        Set<String> departure = new TreeSet<>();
        for(Trip trip : trips) {
            departure.add(trip.from().name());
            this.trips.add(trip);
        }
        // checking that all departure-stations have the same name
        Preconditions.checkArgument(departure.size() == 1);

        // stock visual representation of ticket
        text = computeText(trips);
    }

    /**
     * secondary constructor
     * creates a single-trip ticket
     */
    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(new Trip(from, to, points)));
    }

    /**
     * paste the visual representation of the ticket
     * @return (String) text
     */
    public String text() { return text; }

    /**
     * create String of visual representation of the ticket
     * called in the primary constructor
     * @return (String) visual representation of ticket
     */
    private static String computeText(List<Trip> trips) {
        // name of the departure-station
        String fromStation = trips.get(0).from().toString();

        // create TreeSet and add all destination-stations
        Set<String> names = new TreeSet<>();
        for(Trip trip : trips) {
            names.add(trip.to().toString() + " (" + trip.points() + ")");
        }

        Trip firstTrip = trips.get(0);

        // return String with visual representation of ticket
        // chain name of departure-station to list of destination-stations
        if(trips.size() == 1) {
            return String.format("%s - %s",fromStation,firstTrip.to().toString() + " (" + firstTrip.points() + ")");
        } else {
            return String.format("%s - {%s}",fromStation,String.join(", ",names));
        }
    }

    /**
     * points attributed once a trip on the ticket is finished
     * @param connectivity :
     * @return (int) points
     */
    public int points(StationConnectivity connectivity) {
        List<Integer> temp = new ArrayList<Integer>();
        for(Trip trip : trips) {
            temp.add(trip.points(connectivity));
        }
        return Collections.max(temp);
    }

    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

    @Override
    public String toString() { return text(); }
}
