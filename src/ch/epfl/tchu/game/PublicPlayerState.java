package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * @author Emma Poggiolini (330757)
 */
public class PublicPlayerState {
    // number of tickets
    private final int ticketCount;
    // number of cards
    private final int cardCount;
    // number of wagons left
    private final int carCount;
    // total points obtained from construction of routes
    private final int claimPoints;
    // list of routes built
    private final List<Route> routes;

    /**
     * PublicPlayerState constructor
     * @param ticketCount : number of tickets owned by the player
     * @param cardCount : number of cards owned by the player
     * @param routes : list of routes owned by the player
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        // number of tickets and cards must be non-negative
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        // immutable copy of routes
        this.routes = List.copyOf(routes);
        // initial number of wagons
        int carCountTemp = Constants.INITIAL_CAR_COUNT;
        // total points from construction of routes
        int claimPointsTemp = 0;
        for(Route r : routes) {
            carCountTemp -= r.length();
            claimPointsTemp += r.claimPoints();
        }
        // number of wagons left
        carCount = carCountTemp;
        // total points from the routes
        claimPoints = claimPointsTemp;
    }

    /**
     * Getter for number of tickets owned by the player
     * @return (int) ticketCount
     */
    public int ticketCount() { return ticketCount; }

    /**
     * Getter for number of cards owned by the player
     * @return (int) cardCount
     */
    public int cardCount() { return cardCount; }

    /**
     * Getter for routes owned by the player
     * @return (List<Route>) routes
     */
    public List<Route> routes() { return routes; }

    /**
     * Getter for number of wagons left to the player
     * @return (int) carCount
     */
    public int carCount() { return carCount; }

    /**
     * Getter for number of construction points obtained by the player
     * @return (int) claimPoints
     */
    public int claimPoints() { return claimPoints; }
}
