package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) { // TODO can I assume cards are sorted in right order ?
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList());
    }

    public SortedBag<Ticket> tickets() { return tickets; }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(newTickets, this.cards, this.routes());
    }

    public SortedBag<Card> cards() { return cards; }

    public PlayerState withAddedCard(Card card) {
        return new PlayerState(this.tickets, this.cards.union(SortedBag.of(card)), this.routes());
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes());
    }

    public boolean canClaimRoute(Route route) {
        return this.routes().size() <= this.carCount() && !possibleClaimCards(route).isEmpty();
        // TODO unnecessary to check wagons again because they're already checked in possibleClaimCards()
    }

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
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        // check initialCards isn't empty and doesn't contain more than two types of cards
        Preconditions.checkArgument(!initialCards.isEmpty() && !(initialCards.toSet().size() > 2));
        // check drawnCards are 3
        Preconditions.checkArgument(drawnCards.size() == 3);


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
