package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author
 *
 * All the methods of Player were implemented in this class
 * to play completely randomly
 */
public abstract class PlayerAI implements Player {
    private final Random rng;
    private PlayerState ownState;
    private PublicGameState gameState;
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;
    private SortedBag<Ticket> initialTickets;

    public PlayerAI() {
        this.rng = new Random();
        this.routeToClaim = null;
    }

    public PlayerAI(int seed) { //TODO set this from the main menu
        this.rng = new Random(seed);
        this.routeToClaim = null;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    }

    @Override
    public void receiveInfo(String info) {
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.ownState = ownState;
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        this.initialTickets = tickets;
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        SortedBag.Builder<Ticket> chosenTicketsBuilder = new SortedBag.Builder<>();
        SortedBag<Ticket> options = initialTickets;
        int numberOfTickets = rng.nextInt(5);
        for (int i = 0; i <= numberOfTickets; ++i) {
            int randomIndex = rng.nextInt(5 - i);
            Ticket currentTicket = options.get(randomIndex);
            chosenTicketsBuilder.add(currentTicket);
            options = options.difference(SortedBag.of(currentTicket));
        }
        return chosenTicketsBuilder.build();
    }

    @Override
    public TurnKind nextTurn() {
        List<TurnKind> possibleTurns = new ArrayList<>(TurnKind.ALL);
        assert gameState.canDrawTickets() || possibleTurns.remove(TurnKind.DRAW_TICKETS); //TODO remove assertions
        assert !getAvailableRoutes().isEmpty() || possibleTurns.remove(TurnKind.CLAIM_ROUTE);
        return possibleTurns.get(rng.nextInt(possibleTurns.size()));
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

    protected List<Route> getAvailableRoutes(){
        List<Route> allRoutes = new ArrayList<>(ChMap.routes());
        List<Route> unavailableRoutes = gameState.claimedRoutes();
        allRoutes.removeAll(unavailableRoutes);
        allRoutes = allRoutes.stream().filter(r -> ownState.canClaimRoute(r)).collect(Collectors.toList());
        return allRoutes;
    }
}