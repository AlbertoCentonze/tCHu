package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip from a departure station to an arrival station
 * @author Alberto Centonze (327267)
 */
public final class Trip {
    // departure station
    private final Station from;
    // arrival station
    private final Station to;
    // number of points the trip is worth
    private final int points;

    /**
     * Trip constructor
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points the trip is worth
     * @throws IllegalArgumentException if the number of points is non-positive
     * @throws NullPointerException if the stations are null
     */
    public Trip(Station from, Station to, int points) {
        // check that the stations are null and the points are positive
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Getter for the departure station
     * @return (Station) departure station
     */
    public Station from() {
        return from;
    }

    /**
     * Getter for the arrival station
     * @return (Station) arrival station
     */
    public Station to() {
        return to;
    }

    /**
     * Getter for the number of points of the Trip
     * @return (int) number of points
     */
    public int points() {
        return points;
    }

    /**
     * Returns the number of points if the stations are
     * connected or its value * -1 if they're not
     * @param connectivity : partition containing all the connections
     * @return (int) number of points
     */
    public int points(StationConnectivity connectivity) {
        int sign = connectivity.connected(from, to) ? 1 : -1;
        return sign * points;
    }

    /**
     * List with all the possible trips from a list of stations to another
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points
     * @return (List<Trip>) list with all the possible trips
     * @throws NullPointerException if the stations are null
     * @throws IllegalArgumentException if the number of points is non-positive
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        // check that the stations are null and the points are positive
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points > 0);

        List<Trip> trips = new ArrayList<>();
        for (Station startingStation : from){
            for (Station endingStation : to){
                trips.add(new Trip(startingStation, endingStation, points));
            }
        }
        return trips;
    }
}
