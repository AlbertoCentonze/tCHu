package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
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
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList());
    }

    /**
     * Getter for player's tickets
     * @return (SortedBag<Ticket>) tickets
     */
    public SortedBag<Ticket> tickets() { return this.tickets; }

    /**
     * Player with additional tickets
     * @param newTickets : new tickets added to the player's tickets
     * @return (PlayerState) player with additional tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes());
    }

    /**
     * Getter for player's cards
     * @return (SortedBag<Card>) cards
     */
    public SortedBag<Card> cards() { return this.cards; }

    /**
     * Player with new card
     * @param card : new card
     * @return (PlayerState) player with additional card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(this.tickets, this.cards.union(SortedBag.of(card)), this.routes());
    }

    /**
     * Player with new cards
     * @param additionalCards : new cards
     * @return (PlayerState) player with additional cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes());
    }

    /**
     * Determine whether the player can build the route
     * @param route : route that the player wants to build
     * @return (boolean) true if the player can build the route
     */
    public boolean canClaimRoute(Route route) {
        return !possibleClaimCards(route).isEmpty();
        // this.routes().size() <= this.carCount() &&
        // TODO unnecessary to check wagons again because they're already checked in possibleClaimCards()
    }

    /**
     * List of all possible cards that the player could use to build the route
     * @param route : route that the player wants to build
     * @return (List<SortedBag<Card>>) list of possible cards
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        // check that player has enough wagons to build route
        Preconditions.checkArgument(this.routes().size() <= this.carCount());

        List<SortedBag<Card>> possibleClaimCardsOfPlayer = new ArrayList<>();
        // find the possibleClaimCards for a route that are contained in the player's cards
        for(SortedBag<Card> possibleClaimCards : route.possibleClaimCards()) {
            if(cards.contains(possibleClaimCards)) {
                possibleClaimCardsOfPlayer.add(possibleClaimCards);
            }
        }
        return possibleClaimCardsOfPlayer;
    }


    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        // check additional cards are between 1 and 3
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        // check initialCards isn't empty and doesn't contain more than two types of cards
        Preconditions.checkArgument(!initialCards.isEmpty() && !(initialCards.toSet().size() > 2));
        // check drawnCards are 3
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);


        List<Card> sameTypeAsInitialCardsList = this.cards.toList().stream()
                .filter(elem -> elem.equals(Card.LOCOMOTIVE) || elem.equals(initialCards.get(0)))
                .collect(Collectors.toList());

        // remove initialCards from the SortedBag created
        SortedBag<Card> remainingCards = SortedBag.of(sameTypeAsInitialCardsList).difference(initialCards);
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
        List<Route> withNewRoute = new ArrayList<>(this.routes());
        withNewRoute.add(route);
        // take away claimCards (cards used to claim route) from the player's cards
        return new PlayerState(this.tickets, this.cards.difference(claimCards), List.copyOf(withNewRoute));
    }

    public int ticketPoints() {
        // find maximum station id
        Set<Station> allStations = new HashSet<>();
        for(Route r : routes()) {
            allStations.addAll(r.stations());
        }
        int idMax = allStations.stream()
                .map(Station::id)
                .reduce(0,(idMaxTemp, element) -> element > idMaxTemp ? element : idMaxTemp) + 1;
        // .collect(Collectors.toList())
        // int idMax = Collections.max(ids) + 1;

        StationPartition.Builder builder = new StationPartition.Builder(idMax);
        routes().forEach(r -> builder.connect(r.station1(), r.station2()));
        StationPartition partitions = builder.build();

        int ticketPoints = 0;
        for(Ticket ticket : tickets) {
            ticketPoints += ticket.points(partitions);
        }
        return ticketPoints;
    }

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
