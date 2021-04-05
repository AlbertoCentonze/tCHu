package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicGameState {
    private final PlayerId currentPlayerId;
    private final PlayerId lastPlayer;
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final Map<PlayerId, PublicPlayerState> playerState;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0);
        if (cardState == null || currentPlayerId == null || playerState == null){
            throw new NullPointerException();
        }
        Preconditions.checkArgument(playerState.size() == 2);
        this.playerState = playerState;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
    }

    public boolean canDrawTickets() {
        return this.ticketsCount > 0;
    }

    public PublicCardState cardState(){
        return this.cardState;
    }

    //TODO comment
    public boolean canDrawCards(){
        System.out.println("-------------------" + (cardState.deckSize() + cardState.discardsSize()));
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    public PlayerId currentPlayerId(){
        return this.currentPlayerId;
    }

    public int ticketsCount(){
        return this.ticketsCount;
    }

    public PublicPlayerState playerState(PlayerId playerId){
        return this.playerState.get(playerId);
    }

    public PublicPlayerState currentPlayerState(){
        return this.playerState.get(this.currentPlayerId);
    }

    public List<Route> claimedRoutes(){
        List<Route>  claimedRoutes = new ArrayList<>();
        for (PublicPlayerState ps: playerState.values()){
            claimedRoutes.addAll(ps.routes());
        }
        return claimedRoutes;
    }

    public PlayerId lastPlayer(){
        return this.lastPlayer;
    }
}
