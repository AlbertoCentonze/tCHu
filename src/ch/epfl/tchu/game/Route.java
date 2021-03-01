package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

import static ch.epfl.tchu.game.Constants.MAX_ROUTE_LENGTH;
import static ch.epfl.tchu.game.Constants.MIN_ROUTE_LENGTH;

public final class Route {

    public enum Level { OVERGROUND, UNDERGROUND }

    // identity of the route
    private final String id;
    // first station
    private final Station station1;
    // second station
    private final Station station2;
    // length of the route (number of wagons required to build it)
    private final int length;
    // level of the route
    private final Level level;
    // color of the wagons needed to build the route
    // null implies neutral color
    private final Color color;

    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        // check that the two stations are not equal
        Preconditions.checkArgument(station1.equals(station2));
        // check that the route's length respects the limits
        Preconditions.checkArgument(length >= MIN_ROUTE_LENGTH && length <= MAX_ROUTE_LENGTH);
        // check that the id, stations, level aren't null
        Objects.requireNonNull(id); // TODO is this the best way?
        Objects.requireNonNull(station1);
        Objects.requireNonNull(station2);
        Objects.requireNonNull(level);

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    // returns null if the route is neutral
    public Color color() {
        return color;
    }
}
