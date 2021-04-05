package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestPlayer implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    public int turnCounter;
    private PlayerState ownState;
    private PublicGameState gameState;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;
    private SortedBag<Ticket> initialTickets;

    public TestPlayer(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
        this.routeToClaim = null;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        // TODO System.out.println("initPlayer was called");
    }

    @Override
    public void receiveInfo(String info) {
        // TODO System.out.println("Received info " + info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.ownState = ownState;
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        this.initialTickets = tickets;
        System.out.println(gameState.currentPlayerId().toString() + "received the following tickets" + System.lineSeparator() + tickets.toString());
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        SortedBag.Builder<Ticket> chosenTicketsBuilder = new SortedBag.Builder<>();
        SortedBag<Ticket> options = initialTickets;
        int numberOfTickets = rng.nextInt(5);
        for (int i = 0; i <= numberOfTickets; ++i){
            int randomIndex = rng.nextInt(5 - i);
            Ticket currentTicket = options.get(randomIndex);
            chosenTicketsBuilder.add(currentTicket);
            options = options.difference(SortedBag.of(currentTicket));
        }
        return chosenTicketsBuilder.build();
    }

    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Infinite game");

        List<Route> claimableRoutes = getAvailableRoutes();
        if (gameState.canDrawTickets() && rng.nextFloat() < 0){
            //TODO System.out.println("Drawing tickets");
            return TurnKind.DRAW_TICKETS;
        }
        if (claimableRoutes.isEmpty()) {
            return TurnKind.DRAW_CARDS;
        } else {
            int routeIndex = rng.nextInt(claimableRoutes.size());
            Route route = claimableRoutes.get(routeIndex);
            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            this.routeToClaim = route;
            initialClaimCards = cards.get(rng.nextInt(cards.size()));
            return TurnKind.CLAIM_ROUTE;
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        SortedBag.Builder<Ticket> chosenTicketsBuilder = new SortedBag.Builder<>();
        int numberOfTickets = rng.nextInt(3);
        for (int i = 0; i <= numberOfTickets; ++i){
            int randomIndex = rng.nextInt(3 - i);
            Ticket currentTicket = options.get(randomIndex);
            chosenTicketsBuilder.add(currentTicket);
            options = options.difference(SortedBag.of(currentTicket));
        }
        return chosenTicketsBuilder.build();
    }

    @Override
    public int drawSlot() {
        int[] possibleSlots = IntStream.range(-1, 4).toArray();
        return possibleSlots[rng.nextInt(possibleSlots.length)];
    }

    @Override
    public Route claimedRoute() {
        return this.routeToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        List<SortedBag<Card>> options = this.ownState.possibleClaimCards(this.routeToClaim);
        this.initialClaimCards = options.get(rng.nextInt(options.size()));
        return this.initialClaimCards;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        return options.get(rng.nextInt(options.size()));
    }

    private List<Route> getAvailableRoutes(){
        List<Route> allRoutes = new ArrayList<>(this.allRoutes);
        List<Route> unavailableRoutes = gameState.claimedRoutes();
        allRoutes.removeAll(unavailableRoutes);
        allRoutes = allRoutes.stream().filter(r -> ownState.canClaimRoute(r)).collect(Collectors.toList());
        return allRoutes;
    }
}