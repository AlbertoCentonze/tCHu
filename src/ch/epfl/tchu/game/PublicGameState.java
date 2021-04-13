package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alberto Centonze (327267)
 */
public class PublicGameState {
    // id of the current player
    private final PlayerId currentPlayerId;
    // id of the player of the last turn
    private final PlayerId lastPlayer;
    // number of tickets in the deck of tickets
    private final int ticketsCount;
    // public cardState
    private final PublicCardState cardState;
    // map associating player ids to the public playerState
    private final Map<PlayerId, PublicPlayerState> playerState;

    /**
     * PublicGameState Constructor
     * @param ticketsCount : number of tickets in deck of tickets
     * @param cardState : public cardState
     * @param currentPlayerId : id of the current player
     * @param playerState : map of player ids associated to public playerStates
     * @param lastPlayer : id of the last player of the game
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0);
        if (cardState == null || currentPlayerId == null || playerState == null){
            throw new NullPointerException();
        } // TODO nullPointerException ?
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);
        this.playerState = playerState; // TODO Map.copy ?
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
    }

    /**
     * Establishing if there are enough tickets left to draw from the deck of tickets
     * @return (boolean) true if there is at least one ticket
     */
    public boolean canDrawTickets() {
        return this.ticketsCount > 0;
    }

    /**
     * Getter for public cardState
     * @return (PublicCardState) cardState
     */
    public PublicCardState cardState(){
        return this.cardState;
    }

    /**
     * Establishing if cards can be drawn from the deck of cards
     * @return (boolean) true if the sizes of the deck of cards and discard pile add up to at least five cards
     */
    public boolean canDrawCards(){
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * Getter for the current player's id
     * @return (PlayerId) currentPlayerId
     */
    public PlayerId currentPlayerId(){
        return this.currentPlayerId;
    }

    /**
     * Number of tickets in the deck of tickets
     * @return (int) number of tickets left
     */
    public int ticketsCount(){
        return this.ticketsCount;
    }

    /**
     * Getter for public playerState
     * @param playerId : id of the desired player
     * @return (PublicPlayerState) playerState associated to the playerId
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return this.playerState.get(playerId);
    }

    /**
     * Getter for the current public playerState
     * @return (PublicPlayerState) playerState associated to the currentPlayerId
     */
    public PublicPlayerState currentPlayerState(){
        return this.playerState.get(this.currentPlayerId);
    }

    /**
     * List of all the routes claimed in the game
     * @return (List<Route>) all claimed routes
     */
    public List<Route> claimedRoutes(){
        List<Route>  claimedRoutes = new ArrayList<>();
        for (PublicPlayerState ps: playerState.values()){
            claimedRoutes.addAll(ps.routes());
        }
        return claimedRoutes;
    }

    /**
     * Getter for the last player's id
     * @return (PlayerId) lastPlayer id
     */
    public PlayerId lastPlayer(){
        return this.lastPlayer;
    }
}
