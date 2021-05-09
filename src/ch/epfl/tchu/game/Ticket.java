package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Ticket
 * the player's aims to build the routes that constitute the trips on the ticket
 * @author Emma Poggiolini (330757)
 */
public final class Ticket implements Comparable<Ticket> {
    // list of trips written on the ticket
    private final List<Trip> trips;
    // text on the ticket
    private final String text;

    /**
     * Primary Ticket constructor
     * @param trips : list of trips on the ticket
     * @throws IllegalArgumentException if the list of trips is empty
     * @throws IllegalArgumentException if the departure stations are not the same
     */
    public Ticket(List<Trip> trips) {
        // check that the list isn't empty
        Preconditions.checkArgument(!trips.isEmpty());

        Set<String> departure = new TreeSet<>();
        for(Trip trip : trips) {
            departure.add(trip.from().name());
        }
        // check that all departure-stations have the same name
        Preconditions.checkArgument(departure.size() == 1);

        // initialize the list of trips
        this.trips = List.copyOf(trips);
        // stock visual representation of ticket
        text = computeText(trips);
    }

    /**
     * Secondary Ticket constructor
     * creates a single-trip ticket
     * @param from : departure station
     * @param to : arrival station
     * @param points : points that the ticket is worth
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Paste the visual representation of the ticket
     * @return (String) text
     */
    public String text() { return text; }

    /**
     * Create String of visual representation of the ticket
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
     * Points attributed by the ticket once the game is over
     * negative points if any trips on the ticket have not been built
     * positive points if all trips on the ticket have been built
     * @param connectivity : connectivity
     * @return (int) points
     */
    public int points(StationConnectivity connectivity) {
        List<Integer> temp = new ArrayList<>();
        for(Trip trip : trips) {
            temp.add(trip.points(connectivity));
        }
        return Collections.max(temp);
    }

    @Override
    public int compareTo(Ticket that) {
        return text().compareTo(that.text());
    }

    @Override
    public String toString() { return text(); }
}
