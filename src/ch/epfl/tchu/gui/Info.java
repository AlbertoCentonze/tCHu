package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

public final class Info { // TODO do we ever check that the arguments are not empty ?

    // name of player
    private final String player;

    // list of card names in the same order as the cards in the class Card
    private static final String[] CARD_NAMES = {StringsFr.BLACK_CARD, StringsFr.VIOLET_CARD, StringsFr.BLUE_CARD, StringsFr.GREEN_CARD, StringsFr.YELLOW_CARD, StringsFr.ORANGE_CARD, StringsFr.RED_CARD, StringsFr.WHITE_CARD, StringsFr.LOCOMOTIVE_CARD};

    /**
     * Constructor of Info
     * @param playerName
     */
    public Info(String playerName){
        player = playerName;
    }

    /**
     * String of the name of the card/s
     * @param card
     * @param count
     * @return (String) name of the card
     */
    public static String cardName(Card card, int count){ // TODO switch-case
        return CARD_NAMES[card.ordinal()]+StringsFr.plural(count);
    }

    /**
     * Message with names of the players, who finished the game in a tie, and their number of points
     * @param playerNames
     * @param points
     * @return (String) message of tie
     */
    public static String draw(List<String> playerNames, int points){ // TODO is it okay for us to choose what to write in the message ?
        return String.format(StringsFr.DRAW, playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1), points);
    }

    /**
     * Message saying player will play first
     * @return (String) message
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, player);
    }

    /**
     * Message with number of tickets kept by the player
     * @param count
     * @return (String) message
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, StringsFr.plural(count));
    }

    /**
     * Message saying the player can play
     * @return (String) message
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, player);
    }

    /**
     * Message with number of tickets drawn by the player
     * @param count
     * @return (String) message
     */
    public String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS, player, count, StringsFr.plural(count));
    }

    /**
     * Message saying the player drew a card from the deck
     * @return (String) message
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, player);
    }

    /**
     * Message saying the player drew one of the 5 visible cards
     * @param card
     * @return (String) message
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, player, cardName(card,1));
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

    /**
     * Message saying the player did not seize the route
     * @param route
     * @return (String) message
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, nameRoute(route));
    }

    /**
     * Message saying that the last turn begins
     * @param carCount
     * @return (String) message
     */
    public String lastTurnBegins(int carCount){
        if(carCount > 2) {
            return ""; // TODO do I need to check this condition ?
        } else {
            return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, StringsFr.plural(carCount));
        }
    }

    /**
     * Message saying the player gets the 10-point bonus for owning the longest trail
     * @param longestTrail
     * @return (String) message
     */
    public String getsLongestTrailBonus(Trail longestTrail){ // TODO do I need to check that it's the longest trail ?
        return String.format(StringsFr.GETS_BONUS, player, longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().name());
    }

    /**
     * Message saying player won and adversary lost, and their respective points
     * @param points
     * @param loserPoints
     * @return (String) message
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, player, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * Name of route (departure station - destination station)
     * @param route
     * @return (String) name of route
     */
    private static String nameRoute(Route route){
        return String.format("%s - %s", route.station1().name(), route.station2().name()); // TODO StringFr.EN_DASH_SEPARATOR
    }
}
