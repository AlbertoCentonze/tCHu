package ch.epfl.tchu.game;

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

    @Override
    public TurnKind nextTurn() {
        if(gameState.canDrawTickets() && (ownState.tickets().size() < MAX_NUMBER_OF_TICKETS)
                && (rng.nextInt(PROBABILITY_DRAW_TICKET) == 0)) { // small chance of drawing tickets
            return TurnKind.DRAW_TICKETS;
        } else if(getAvailableRoutes().stream().anyMatch(r -> ownState.canClaimRoute(r))) {
            // claims route as soon as there is one available and the player can claim it
            return TurnKind.CLAIM_ROUTE;
        } else { // otherwise draws card (most frequent action)
            return TurnKind.DRAW_CARDS; // TODO canDrawCard() to be checked?
        }
    }
}
