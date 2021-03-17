package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicPlayerState {
    // number of tickets
    private final int ticketCount; // TODO final ? just not-mutable
    // number of cards
    private final int cardCount;
    // number of wagons left
    private final int carCount;
    // total points obtained from construction of routes
    private final int claimPoints;
    // list of routes built
    private final List<Route> routes;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        int claimPoints1;
        // number of tickets and cards must be non-negative
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        // copy of routes
        this.routes = List.copyOf(routes);
        // number of wagons left
        int carCountTemp = 40;
        // total points from construction of routes
        int claimPointsTemp = 0;
        for(Route r : routes) {
            carCountTemp -= r.length();
            claimPointsTemp += r.claimPoints();
        }
        carCount = carCountTemp;
        claimPoints = claimPointsTemp;
    }

    public int ticketCount() { return ticketCount; }

    public int cardCount() { return cardCount; }

    public List<Route> routes() { return routes; } // TODO I already made an immutable copy, no need for another one right ?
 // TODO try to modify in test
    public int carCount() { return cardCount; }

    public int claimPoints() { return claimPoints; }
}
