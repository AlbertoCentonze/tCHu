package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) { // TODO can I assume cards are sorted in right order ?
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        // TODO do I need to save routes again ?
    }

    // TODO public
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>()); // TODO new ArrayList
    }

    public SortedBag<Ticket> tickets() { return tickets; }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(newTickets, this.cards, this.routes()); // TODO inherited method
    }

    public SortedBag<Card> cards() { return cards; }

    public PlayerState withAddedCard(Card card) {
        return new PlayerState(this.tickets, this.cards.union(SortedBag.of(card)), this.routes()); // TODO union
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes()); // TODO union
    }

    public boolean canClaimRoute(Route route) {
        return this.routes().size() <= this.carCount() && !possibleClaimCards(route).isEmpty();
        // TODO unnecessary to check wagons again because they're already checked in possibleClaimCards()
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        // check that player has enough wagons to build route
        Preconditions.checkArgument(this.routes().size() <= this.carCount());

        List<SortedBag<Card>> possibleClaimCardsOfPlayer = new ArrayList<>(); // TODO ArrayList
        // find the possibleClaimCards for a route that are contained in the player's cards
        for(SortedBag<Card> possibleClaimCards : route.possibleClaimCards()) {
            if(cards.contains(possibleClaimCards)) { // TODO works ? a SortedBag contains another SortedBag
                possibleClaimCardsOfPlayer.add(possibleClaimCards);
            }
        }
        return possibleClaimCardsOfPlayer;
    }


    // TODO complete method
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        return ;
    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        // add new route to player's routes
        List<Route> withNewRoute = new ArrayList<>(this.routes());
        withNewRoute.add(route);
        // take away claimCards (cards used to claim route) from the player's cards
        return new PlayerState(this.tickets, this.cards.difference(claimCards), List.copyOf(withNewRoute)); // TODO difference
    }

    public int ticketPoints() {
        int ticketPoints = 0;
        for(Ticket ticket : tickets) {
            ticketPoints += ticket.points(); // TODO connectivity
        }
        return ticketPoints;
    }

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
