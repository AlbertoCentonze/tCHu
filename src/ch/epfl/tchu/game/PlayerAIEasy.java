package ch.epfl.tchu.game;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unsophisticated Artificial Intelligence Player
 * claims a route randomly whenever possible
 * accumulates 10 tickets maximum
 * does not aim to build the trails on the tickets
 * draws a card randomly otherwise
 */
public class PlayerAIEasy extends PlayerAI {

    private static final int MAX_NUMBER_OF_TICKETS = 10;
    private static final int PROBABILITY_DRAW_TICKET = 10;

    /**
     * Constructor
     * @param seed can be null
     */
    public PlayerAIEasy(Integer seed) {
        super(seed);
    }

    @Override
    public TurnKind nextTurn() {
        if(gameState.canDrawTickets() && (ownState.tickets().size() < MAX_NUMBER_OF_TICKETS)
                && (rng.nextInt(PROBABILITY_DRAW_TICKET) == 0)) { // small chance of drawing tickets
            return TurnKind.DRAW_TICKETS;
        }
        updateClaimable();
        if(!claimable.isEmpty()) {
            routeToClaim = claimable.get(rng.nextInt(claimable.size()));
            // claims route as soon as there is one available and the player can claim it
            return TurnKind.CLAIM_ROUTE;
        } else if(gameState.canDrawCards()) { // otherwise draws card
            return TurnKind.DRAW_CARDS;
        } else { // default value
            return TurnKind.DRAW_TICKETS;
        }
    }
}
