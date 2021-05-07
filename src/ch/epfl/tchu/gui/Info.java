package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emma Poggiolini (330757)
 */

/**
 * Class that manages the messages communicated to the players
 */
public final class Info {
    // name of player
    private final String player;
    // list of card names in the same order as the cards in the class Card
    private static final String[] CARD_NAMES = {StringsFr.BLACK_CARD, StringsFr.VIOLET_CARD, StringsFr.BLUE_CARD, StringsFr.GREEN_CARD, StringsFr.YELLOW_CARD, StringsFr.ORANGE_CARD, StringsFr.RED_CARD, StringsFr.WHITE_CARD, StringsFr.LOCOMOTIVE_CARD};

    /**
     * Constructor of Info
     * @param playerName : name of the player
     */
    public Info(String playerName){
        player = playerName;
    }

    /**
     * String of the name of the card/s
     * @param card : card
     * @param count : number of specified cards
     * @return (String) name of the card
     */

    public static String cardName(Card card, int count) {
        return CARD_NAMES[card.ordinal()] + StringsFr.plural(count);
    }

    /**
     * Message with names of the players, who finished the game in a tie, and their number of points
     * @param playerNames : list of names of the players
     * @param points : points gained by the two players (same amount)
     * @return (String) message of tie
     */
    public static String draw(List<String> playerNames, int points){
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
     * @param count : number of tickets kept byt the player
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
     * @param count : number of tickets drawn by the player
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
     * @param card : card drawn
     * @return (String) message
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, player, cardName(card,1));
    }

    /**
     * Message saying the player claimed the route and with which cards
     * @param route : route claimed by the player
     * @param cards : cards used to claim the route
     * @return (String) message
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE, player, nameRoute(route), cardsInSortedBag(cards));
    }

    /**
     * Message saying the player attempted to claim the tunnel and with which cards
     * @param route : tunnel that the player attempts to claim
     * @param initialCards : cards intended to claim the tunnel
     * @return (String) message
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, nameRoute(route), cardsInSortedBag(initialCards));
    }

    /**
     * Message listing the cards drawn by the player, and stating how many additional cards must be used to build
     * @param drawnCards : additional cards drawn from to deck when attempting to claim the tunnel
     * @param additionalCost : number of cards the player needs to add to claim the tunnel
     * @return (String) message
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String addedCost = (additionalCost > 0 ? String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)) : StringsFr.NO_ADDITIONAL_COST);
        return String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsInSortedBag(drawnCards)) + addedCost;
    }

    /**
     * Message saying the player did not seize the route
     * @param route : route that the player did not manage to claim
     * @return (String) message
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, nameRoute(route));
    }

    /**
     * Message saying that the last turn begins
     * @param carCount : number of player's wagons left
     * @return (String) message
     */
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, StringsFr.plural(carCount));
    }

    /**
     * Message saying the player gets the 10-point bonus for owning the longest trail
     * @param longestTrail : longest trail built during the game
     * @return (String) message
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        return String.format(StringsFr.GETS_BONUS, player, longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().name());
    }

    /**
     * Message saying player won and adversary lost, and their respective points
     * @param points : number of points gained by the player (who is the winner)
     * @param loserPoints : number of points gained by the other player (who is the loser)
     * @return (String) message
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, player, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * Name of route (departure station - destination station)
     * @param route : route
     * @return (String) name of route
     */
    private static String nameRoute(Route route){
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    /**
     * String listing the name of the cards and their multiplicities, in the same order as in the enum Card
     * @param cards : cards to be listed
     * @return (String) list of cards
     */
    protected static String cardsInSortedBag(SortedBag<Card> cards) { // TODO protected
        List<String> listOfCards = new ArrayList<>();
        for(Card c : cards.toSet()) {
            int n = cards.countOf(c);
            listOfCards.add(n + " " + cardName(c,n));
        }
        int finalCard = listOfCards.size()-1;
        return (finalCard > 0 ?
                String.join(", ", listOfCards.subList(0, finalCard)) + StringsFr.AND_SEPARATOR + listOfCards.get(finalCard)
                : listOfCards.get(finalCard));
    }
}
