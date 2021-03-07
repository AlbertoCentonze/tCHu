package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

public final class Info {
    public Info(String playerName){
        //TODO
    }

    public static String cardName(Card card, int count){
        return null; //TODO
    }

    public static String draw(List<String> playerNames, int points){
        return null; //TODO
    }

    public String willPlayFirst(){
        return null; //TODO
    }

    public String keptTickets(int count){
        return null; //TODO
    }

    public String canPlay(){
        return null; //TODO
    }

    public String drewTickets(int count){
        return null; //TODO
    }

    public String drewBlindCard() {
        return null; //TODO
    }

    public String DrewVisibleCard(Card card){
        return null; //TODO
    }

    public String claimedRoute(Route route, SortedBag<Card> cards){
        return null; //TODO
    }

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return null; //TODO
    }

    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        return null; //TODO
    }

    public String didNotClaimRoute(Route route){
        return null; //TODO
    }

    public String lastTurnBegins(int carCount){
        return null; //TODO
    }

    public String getLongestTrailBonus(Trail longestTrail){
        return null; //TODO
    }

    public String won(int points, int loserPoints){
        return null; //TODO
    }
}
