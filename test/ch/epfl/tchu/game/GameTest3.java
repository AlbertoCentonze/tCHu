package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class GameTest3 {

    @Test
    void testOnPlay(){
        Map<PlayerId, Player> players = new HashMap<>();
        players.put(PlayerId.PLAYER_1, new TestPlayer(342, ChMap.routes()));
        players.put(PlayerId.PLAYER_2, new TestPlayer(596, ChMap.routes()));
        Map<PlayerId, String> playerNames = new HashMap<>();
        playerNames.put(PlayerId.PLAYER_1, "Alice");
        playerNames.put(PlayerId.PLAYER_2, "Bob");
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        Game.play(players, playerNames, tickets, rng);
    }




    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 400;

        private int card;

        private final Random rng;
        private SortedBag<Ticket> tickets;
        private String name;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;


        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            card = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){
            name = playerNames.get(ownId);
        }

        public void receiveInfo(String info){
            System.out.println(name + " ("+card+") : " + info);
            /*if(ownState != null && routeToClaim != null){
                System.out.println(ownState.cards());
                System.out.println(routeToClaim.possibleClaimCards());
                System.out.println(ownState.canClaimRoute(routeToClaim));
                System.out.println(ownState.possibleClaimCards(routeToClaim));
                System.out.println(ownState.carCount());
                if (!ownState.routes().isEmpty()){
                    for(Route r : ownState.routes()){
                        System.out.println(r.id());
                    }
                }
            }
            System.out.println();*/

        }

        public void setInitialTicketChoice(SortedBag<Ticket> tickets){
            this.tickets = tickets;
        }

        public SortedBag<Ticket> chooseInitialTickets(){
            tickets = SortedBag.of(tickets.toList().subList(0,tickets.size()-new Random().nextInt(Constants.DISCARDABLE_TICKETS_COUNT)));
            return tickets;
        }

        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options){
            return SortedBag.of(options.toList().subList(0,options.size()-new Random().nextInt(Constants.DISCARDABLE_TICKETS_COUNT)));
        }

        public int drawSlot(){
            return -1 + new Random().nextInt(5);
        }

        public Route claimedRoute(){
            return routeToClaim;
        }

        public SortedBag<Card> initialClaimCards(){
            if(!initialClaimCards.isEmpty()) {
                /*return ownState.possibleClaimCards(routeToClaim).get(new Random().nextInt((ownState.possibleClaimCards(routeToClaim).size())));*/
                return initialClaimCards;
            }
            else return SortedBag.of();
        }

        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options){
            if(options.isEmpty()) {
                return SortedBag.of();
            }
            return options.get(new Random().nextInt(options.size()));
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            /*List<Route> claimableRoutes = new ArrayList<>();*/
            List<Route> claimableRoutes = allRoutes.stream().filter(ownState::canClaimRoute).collect(Collectors.toList());
            if (claimableRoutes.isEmpty()) {
                card++;
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
    }
}