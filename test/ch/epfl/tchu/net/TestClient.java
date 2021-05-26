package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.security.cert.TrustAnchor;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        SortedBag<Ticket> tickets;

        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.printf("info: %s\n", info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.printf("newState's current player: %s\n", newState.currentPlayerId());
            System.out.printf("ownState's final points: %d\n", ownState.finalPoints());
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.tickets = tickets;
            System.out.printf("initial tickets: %s\n", tickets);
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            SortedBag<Ticket> chosen = SortedBag.of(tickets.toList().subList(0,3));
            System.out.printf("chosen initial tickets: %s\n", chosen);
            return chosen;
        }

        @Override
        public TurnKind nextTurn() { // randomly chosen
            Random x = new Random();
            TurnKind next = TurnKind.values()[x.nextInt(3)];
            System.out.printf("next turn: %s\n", next);
            return next;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            SortedBag<Ticket> chosen = SortedBag.of(options.toList().subList(1,4));
            System.out.printf("chosen tickets: %s\n", chosen);
            return chosen;
        }

        @Override
        public int drawSlot() { // randomly chosen
            int[] slots = {-1,0,1,2,3,4};
            int slot = (new Random()).nextInt(slots.length);
            System.out.printf("slot: %d\n", slots[slot]);
            return slots[slot];
        }

        @Override
        public Route claimedRoute() {
            Route r = ChMap.routes().get(0);
            System.out.printf("route claimed: %s - %s\n", r.station1().name(), r.station2().name());
            return r;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            SortedBag<Card> initial = SortedBag.of(4, Card.BLUE);
            System.out.printf("initial cards to claim route: %s\n", initial);
            return initial;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            SortedBag<Card> additional = options.get(0); // SortedBag.of(1, Card.BLUE, 1, Card.LOCOMOTIVE);
            System.out.printf("additional cards to claim tunnel: %s\n", additional);
            return additional;
        }

        // … autres méthodes de Player
    }
}