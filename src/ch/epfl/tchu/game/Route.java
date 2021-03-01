package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.*;

public final class Route {

    // nested enumeration of the possible levels of a route
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

    /**
     * create list of the two stations in order
     * @return (List<Station>) list of the two stations
     */
    public List<Station> stations() {
        return List.of(station1,station2);
    }

    /**
     * return opposite station to the one in the argument
     * @param station
     * @return (Station) opposite station
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        if(station.equals(station1)) {  // TODO there must be a better way
            return station2;
        } else {
            return station1;
        }
    }

    /**
     * number of points won upon construction of the route
     * @return (int) claim points
     */
    public int claimPoints() {
        return ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * create list of all possible card combinations used to build the route
     * @return (List<SortedBag<Card>>) list of cards
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> cards = new ArrayList<>(); // TODO is the ArrayList right here?

        // colored routes
        if(color != null) {
            // add a SortedBag with:
            // number of cards equal to the length of the route
            // only cards of the color of the route
            cards.add(SortedBag.of(length, Card.of(color)));
        } else { // neutral routes
            for (Color c : Color.ALL) {
                // add a SortedBag with:
                // number of cards equal to the length of the route
                // of every color in the enumerate Color
                cards.add(SortedBag.of(length, Card.of(c)));
            }
        }
        // tunnels
        if(level == Level.UNDERGROUND) {
            // colored tunnels
            if(color != null) {
                // i represents the number of locomotives
                for(int i = 1; i <= length; ++i) {
                    // add a SortedBag with:
                    // number of cards of the route's color equal to the length of the route minus i
                    // number i of locomotives
                    cards.add(SortedBag.of(length - i, Card.of(color), i, Card.of(null))); // TODO in tests check if it works with null
                }
            } else { // neutral tunnels
                for (Color c : Color.ALL) {
                    // i represents the number of locomotives
                    for(int i = 1; i <= length; ++i) {
                        // add a SortedBag with:
                        // number of colored cards equal to the length of the route minus i
                        // number i of locomotives
                        // for every color in the enumerate Color
                        cards.add(SortedBag.of(length - i, Card.of(c), i, Card.of(null)));
                    }
                }
            }
        }
                    //SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
                    //cardBuilder.add(length,Card.of(color));
                    //cards.add(cardBuilder.build());
         return cards;
    }
}
