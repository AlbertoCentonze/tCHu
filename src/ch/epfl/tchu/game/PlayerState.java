package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Emma Poggiolini (330757)
 */

/**
 * Private (and public) part of the state of the player
 * represents the private (and public) player information that unknown to the other player/s
 */
public final class PlayerState extends PublicPlayerState {
    // player's tickets
    private final SortedBag<Ticket> tickets;
    // player's cards
    private final SortedBag<Card> cards;

    /**
     * PlayerState constructor
     * @param tickets : SortedBag of tickets owned by the player
     * @param cards : SortedBag of cards owned by the player
     * @param routes : List of routes owned by the player
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Player at the beginning of the game with 4 initial cards, no tickets, no routes
     * @param initialCards : 4 initial cards
     * @return (PlayerState) new player in initial state
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        // check that there are 4 initial cards
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList());
    }

    /**
     * Getter for player's tickets
     * @return (SortedBag<Ticket>) tickets
     */
    public SortedBag<Ticket> tickets() { return tickets; }

    /**
     * Player with additional tickets
     * @param newTickets : new tickets added to the player's tickets
     * @return (PlayerState) player with additional tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * Getter for player's cards
     * @return (SortedBag<Card>) player's cards
     */
    public SortedBag<Card> cards() { return cards; }

    /**
     * Player with a new card
     * @param card : new card
     * @return (PlayerState) player with additional card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Player with new cards
     * @param additionalCards : new cards
     * @return (PlayerState) player with additional cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    /**
     * Determine whether the player can build the route
     * @param route : route that the player wants to build
     * @return (boolean) true if the player can build the route
     */
    public boolean canClaimRoute(Route route) {
        return route.length() <= carCount() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * List of all possible cards that the player could use to build the route
     * @param route : route that the player wants to build
     * @return (List<SortedBag<Card>>) list of possible cards
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        // check that player has enough wagons to build route
        Preconditions.checkArgument(route.length() <= carCount());

        List<SortedBag<Card>> possibleClaimCardsOfPlayer = new ArrayList<>();
        // find the possibleClaimCards for a route that are contained in the player's cards
        for(SortedBag<Card> possibleClaimCards : route.possibleClaimCards()) {
            if(cards.contains(possibleClaimCards)) {
                possibleClaimCardsOfPlayer.add(possibleClaimCards);
            }
        }
        return possibleClaimCardsOfPlayer;
    }

    /**
     * Compute list of all possible additional cards for building a tunnel
     * @param additionalCardsCount : number of cards to add
     * @param initialCards : cards intended to be used to build tunnel
     * @param drawnCards : cards drawn
     * @return (List<SortedBag<Card>>) list containing all options of cards to add
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        // check additional cards are between 1 and 3
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        // check initialCards isn't empty and doesn't contain more than two types of cards
        Preconditions.checkArgument(!initialCards.isEmpty() && !(initialCards.toSet().size() > 2));
        // check drawnCards are 3
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        // select all the player's locomotive cards and cards of the same type as the initialCards
        List<Card> sameTypeAsInitialCardsList = cards.toList().stream()
                .filter(elem -> elem.equals(Card.LOCOMOTIVE) || elem.equals(initialCards.get(0)))
                .collect(Collectors.toList());

        // remove initialCards from the SortedBag created
        SortedBag<Card> remainingCards = SortedBag.of(sameTypeAsInitialCardsList).difference(initialCards);
        // not enough cards to add
        if(remainingCards.size() < additionalCardsCount) {
            return Collections.emptyList();
        }
        // subsets of the size of additionalCardsCount containing possible additional cards
        List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(remainingCards.subsetsOfSize(additionalCardsCount));
        // order the cards by increasing number of locomotives
        possibleAdditionalCards.sort(Comparator.comparingInt(element -> element.countOf(Card.LOCOMOTIVE)));
        return possibleAdditionalCards;
    }

    /**
     * Player with new claimed route, without the cards used to claim this route
     * @param route : route claimed by the player
     * @param claimCards : cards used to claim route
     * @return (PlayerState) player with new route, without used cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        // add new route to player's routes
        List<Route> withNewRoute = new ArrayList<>(routes());
        withNewRoute.add(route);
        // take away claimCards (cards used to claim route) from the player's cards
        return new PlayerState(tickets, cards.difference(claimCards), withNewRoute);
    }

    /**
     * Total number of points obtained from player's tickets
     * @return (int) points
     */
    public int ticketPoints() {
        // find maximum station id
        Set<Station> allStations = new HashSet<>();
        for(Route r : routes()) {
            allStations.addAll(r.stations());
        }
        Integer idMax = allStations.stream()
                .map(s -> s.id() + 1).max(Integer::compare).orElse(0);

        // building the station partitions
        StationPartition.Builder builder = new StationPartition.Builder(idMax);
        routes().forEach(r -> builder.connect(r.station1(), r.station2()));
        StationPartition partitions = builder.build();

        return tickets.stream()
                .map(t -> t.points(partitions))
                .reduce(0, Integer::sum);
    }

    /**
     * Total final number of points gained by the player
     * @return (int) points
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
