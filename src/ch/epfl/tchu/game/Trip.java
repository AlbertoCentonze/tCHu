package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Trip { // TODO comment the class !
    private final Station from;
    private final Station to;
    private final int points;

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    public Station from() {
        return from;
    }

    public Station to() {
        return to;
    }

    public int points() {
        return points;
    }

    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(this.from, this.to)){
            return this.points;
        }
        return -this.points;
    }

    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);
        List<Trip> trips = new ArrayList<Trip>();
        for (Station startingStation : from){
            for (Station endingStation : to){
                trips.add(new Trip(startingStation, endingStation, points));
            }
        }
        return trips;
    }
}
