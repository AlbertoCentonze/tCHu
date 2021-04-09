package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;


class GameTest2 {
    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;


        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private PlayerId ownId;
        private Map<PlayerId, String> playerNames;
        private SortedBag<Ticket> initialTickets;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private int infoCounter;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.infoCounter = 0;

        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            System.out.println("turnCounter  " + turnCounter);
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            List<Route> claimableRoutes = new ArrayList<>();
            for (Route route : allRoutes) {
                if (ownState.canClaimRoute(route)) {
                    claimableRoutes.add(route);
                }
            }
            if (turnCounter % 10 == 0 && gameState.ticketsCount()>=3)
                return TurnKind.DRAW_TICKETS;
            else if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;

            }
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId=ownId;
            this.playerNames=playerNames;

        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(info);
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTickets=tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            SortedBag<Ticket> result = SortedBag.of();
            for(int i =0; i<3; i++){
                result = result.union(SortedBag.of(initialTickets.get(i)));
            }
            return result;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return options;
        }

        @Override
        public int drawSlot() {
            return rng.nextInt(6)-1;
        }

        @Override
        public Route claimedRoute() {
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }

    }
    @Test
    void playWorks() {

        TestPlayer player1 = new TestPlayer(20,ChMap.routes());
        TestPlayer player2 = new TestPlayer(20,ChMap.routes());
        Map<PlayerId, Player> players = new HashMap<>();
        players.put(PlayerId.PLAYER_1, player1);
        players.put(PlayerId.PLAYER_2, player2);

        Map<PlayerId, String> playerNames = new HashMap<>();
        playerNames.put(PlayerId.PLAYER_1, "Charles");
        playerNames.put(PlayerId.PLAYER_2, "Alice");

        Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());

    }



}