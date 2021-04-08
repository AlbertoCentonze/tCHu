package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alberto Centonze
 */

public class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Trip constructor
     *
     * @param from the initial station you start from
     * @param to the arrival station you go to
     * @param points the number of points of the trip
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Getter for the departure station
     * @return the instance of the departure station
     */
    public Station from() {
        return from;
    }

    /**
     * Getter for the arrival station
     * @return the instance of the arrival station
     */
    public Station to() {
        return to;
    }

    /**
     * Getter for the number of points of the Trip
     * @return the number of points
     */
    public int points() {
        return points;
    }

    /**
     * Returns the number of points if the stations are
     * connected or its value * -1 if they're not
     * @param connectivity the partition containing all the connections
     * @return the number of points
     */
    public int points(StationConnectivity connectivity) {
        int negative = connectivity.connected(this.from, this.to) ? 1 : -1;
        return negative * this.points;
    }

    /**
     * Return a list with all the possible
     * trips from a list to another one
     * @param from the departure station
     * @param to the arrival station
     * @param points the number of points
     * @return the list with all the possible trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
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
