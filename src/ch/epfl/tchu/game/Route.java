package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * @author Emma Poggiolini (330757)
 * Route connecting two neighboring stations
 */
public final class Route {
    // nested enumeration of the possible levels of a route
    public enum Level { OVERGROUND, UNDERGROUND }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Constructor of Route
     * @param id : id of the route
     * @param station1 : first station
     * @param station2 : second station
     * @param length : length of the route
     * @param level : level of the route
     * @param color : color of the wagons needed to build the route
     * @throws IllegalArgumentException if the first station and second station are equal
     * @throws IllegalArgumentException if the length of the route is shorter than 1 or longer than 6
     * @throws NullPointerException if the id, stations, or level are null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length >= MIN_ROUTE_LENGTH && length <= MAX_ROUTE_LENGTH);
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.color = color;
        this.length = length;
    }

    /**
     * Getter for id
     * @return (String) id
     */
    public String id() {
        return id;
    }

    /**
     * Getter for first station
     * @return (Station) station1
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for second station
     * @return (Station) station2
     */
    public Station station2() {
        return station2;
    }

    /**
     * Getter for route's length
     * @return (int) length
     */
    public int length() {
        return length;
    }

    /**
     * Getter for route's level
     * @return (Level) level
     */
    public Level level() {
        return level;
    }

    /**
     * Getter for route's color
     * @return (Color) color
     */
    // returns null if the route is neutral
    public Color color() {
        return color;
    }

    /**
     * Create list of the two stations in order
     * @return (List<Station>) list of the two stations
     */
    public List<Station> stations() {
        return List.of(station1,station2);
    }

    /**
     * Return opposite station to the one in the argument
     * @param station the initial station
     * @return (Station) opposite station
     * @throws IllegalArgumentException if station does not correspond to either the departure or the arrival station
     */
    public Station stationOpposite(Station station) {
        // check that station corresponds to either the departure or the arrival station
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Number of points won upon construction of the route
     * @return (int) claim points
     */
    public int claimPoints() {
        return ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Create list of all possible card combinations used to build the route
     * @return (List<SortedBag<Card>>) list of cards
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> cards = new ArrayList<>();

        // colored routes
        if(color != null) {
            cards.add(SortedBag.of(length, Card.of(color)));
        } else { // neutral routes
            for (Color c : Color.ALL) {
                cards.add(SortedBag.of(length, Card.of(c)));
            }
        }
        if(level == Level.UNDERGROUND) {
            // Il y a une duplication de code entre les cas coloré/neutre (utiliser `var colors = color == null ? Color.All : List.of(color);` vous aidera à l'éliminer).
            if(color != null) {
                // i represents the number of locomotives
                for(int locomotiveCount = 1; locomotiveCount <= length; ++locomotiveCount)
                    cards.add(SortedBag.of(length - locomotiveCount, Card.of(color), locomotiveCount, Card.LOCOMOTIVE));
            } else {
                // loop on length first to create bags in expected order
                for(int locomotiveCount = 1; locomotiveCount < length; ++locomotiveCount) {
                    for (Color c : Color.ALL)
                        cards.add(SortedBag.of(length - locomotiveCount, Card.of(c), locomotiveCount, Card.LOCOMOTIVE));
                }
                cards.add(SortedBag.of(length, Card.LOCOMOTIVE));
            }
        }
        return cards;
    }

    /**
     * Number of additional cards required to build the tunnel
     * @param claimCards : cards paid to build tunnel
     * @param drawnCards : 3 cards drawn
     * @return (int) number of additional cards
     * @throws IllegalArgumentException if the route is not a tunnel
     * @throws IllegalArgumentException if the drawnCards are not 3
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        // check route is a tunnel
        Preconditions.checkArgument(level == Level.UNDERGROUND);
        // check drawn pack has 3 cards
        Preconditions.checkArgument(drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);

        // set of all the cards intended to claim the tunnel, without the locomotive (if present)
        Set<Card> cardsToClaim = new HashSet<>(claimCards.toSet());
        cardsToClaim.remove(Card.LOCOMOTIVE);
        // number of locomotive cards in the drawn cards
        int count = drawnCards.countOf(Card.LOCOMOTIVE);

        // adding the number of cards that are not locomotives and that
        // are found both in the drawn cards and in the cardsToClaim
        for(Card card : cardsToClaim){
            count += drawnCards.countOf(card);
        }
        return count;
    }
}
