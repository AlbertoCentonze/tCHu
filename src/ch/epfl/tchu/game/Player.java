package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * @author Alberto Centonze (327267)
 * Interface representing a player
 */
public interface Player {
    /**
     * Called at the beginning of the game to give to each player his own Id and names
     * @param ownId : the id of the player
     * @param playerNames : the map containing the string representation of the players' names
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Allows the player to receive an information
     * @param info : the information communicated (usually generated by Info)
     */
    void receiveInfo(String info);

    /**
     * Informs the player that his state has changed
     * @param newState : the new state
     * @param ownState : the previous state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Informs to the player about his initial tickets
     * @param tickets : the tickets of the player
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks to the player which tickets he wants to keep
     * @return (SortedBag<Ticket>) the tickets kept
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Asks to the player what kind of action he wants to take during this turn
     * @return (TurnKind) type of action
     */
    TurnKind nextTurn();

    /**
     * Informs the player about the additionalTickets
     * @param options : the tickets drawn
     * @return (SortedBag<Ticket>) the tickets kept
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Asks to the player the source to draw cards from
     * @return (int) integer between -1 and 4
     * -1 represents the deck
     * [0,4] represent the faceUpCards slots
     */
    int drawSlot();

    /**
     * Called to know which Route the player wants to claim
     * @return (Route) the Route claimed
     */
    Route claimedRoute();

    /**
     * Called to know with which cards the player wants to attempt to claim a Route
     * @return (SortedBag<Card>) the cards chosen by the player
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Gives the player the possible additional card options with which to claim a tunnel
     * @param options : the card options from which to choose the additional cards
     * @return (SortedBag<Card>) an empty SortedBag if the player won't claim the tunnel
     * otherwise the chosen additional cards
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * The various types of turns (actions) that the player can perform
     */
    enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }
}
