package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    private static Map<PlayerId, String> playerNames(){
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        playerNames.put(PlayerId.PLAYER_1, "Alberto");
        playerNames.put(PlayerId.PLAYER_2, "Emma");
        return playerNames;
    }

    private static Map<PlayerId, Player> getRandomPlayers(){
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, new RandomPlayer(new Random().nextInt(100000), ChMap.routes()));
        players.put(PlayerId.PLAYER_2, new RandomPlayer(new Random().nextInt(32477809), ChMap.routes()));
        return players;
    }

    private static Map<PlayerId, Player> getOtherPlayers(){
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, new OtherPlayer());
        players.put(PlayerId.PLAYER_2, new OtherPlayer());
        return players;
    }

    @Test
    void playWorksMultipleTimesWithoutErrorsRandomPlayer(){
        IntStream.range(0, 100).forEach(n ->
                Game.play(getRandomPlayers(), playerNames(), SortedBag.of(ChMap.tickets()), new Random())
                );
}

    @Test
    void playWorksMultipleTimesWithoutErrorsOtherImplementation() {
        IntStream.range(0, 100).forEach(n ->
                Game.play(getOtherPlayers(), playerNames(), SortedBag.of(ChMap.tickets()), new Random())
        );
    }

    @Test
    void suggestedTestPiazza(){
        var p = new RandomPlayer(new Random().nextInt(100000), ChMap.routes()) {
            public int infoCounter = 0;
            public int nextCounter = 0;
            @Override
            public void receiveInfo(String info) {
                this.infoCounter += 1;
                System.out.println("is being called");
                super.receiveInfo(info);
            }

            @Override
            public TurnKind nextTurn() {
                ++nextCounter;
                return super.nextTurn();
            }
        };
        Game.play(getRandomPlayers(), playerNames(), SortedBag.of(ChMap.tickets()), new Random());
        System.out.println(p.infoCounter);
        System.out.println(p.nextCounter);
    }

    @Test
    void testGameWithOtherImplementation() {
        for (int i = 0; i < 100; ++i)
        {
            Map<PlayerId, String> names = Map.of
                    (
                            PlayerId.PLAYER_1, "Joueur 1",
                            PlayerId.PLAYER_2, "Joueur 2"
                    );
            Map<PlayerId, Player> players = Map.of
                    (
                            PlayerId.PLAYER_1, new OtherPlayer(),
                            PlayerId.PLAYER_2, new OtherPlayer()
                    );

            Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random());

            assertTrue(((OtherPlayer) players.get(PlayerId.PLAYER_1)).testIsSuccessful());
            assertTrue(((OtherPlayer) players.get(PlayerId.PLAYER_2)).testIsSuccessful());
        }
    }

    static class NoPossibleAction extends IllegalStateException {}

    static class OtherPlayer implements Player
    {
        private static final int TURN_LIMIT = 1000;
        private final Queue<String> expectedInfoQueue;
        private int turnCount = 0;
        private PlayerId id;
        private String name, otherPlayerName;

        private PlayerState playerState;
        private PublicGameState gameState;

        private SortedBag<Ticket> initialTicketChoice;
        private Route claimedRoute;
        private SortedBag<Card> claimCards;

        private Info info;

        public OtherPlayer()
        {
            expectedInfoQueue = new ArrayDeque<>();
        }
        public boolean testIsSuccessful()
        {
            return expectedInfoQueue.isEmpty();
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames)
        {
            id = ownId;
            name = playerNames.get(id);
            otherPlayerName = playerNames.get(id.next());
            info = new Info(name);
        }
        @Override
        public void receiveInfo(String info)
        {
            if (id == PlayerId.PLAYER_1)
            {
                System.out.print(info);
            }
            if (info.equals(expectedInfoQueue.peek()))
                expectedInfoQueue.poll();
            expectedInfoQueue.forEach(s -> System.out.print("info "+s));
            System.out.println("suivant");
        }
        @Override
        public void updateState(PublicGameState newState, PlayerState ownState)
        {
            gameState = newState;
            playerState = ownState;
        }
        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets)
        {
            initialTicketChoice = tickets;
        }
        @Override
        public SortedBag<Ticket> chooseInitialTickets()
        {
            int chosenTicketsCount = new Random().nextInt(Constants.INITIAL_TICKETS_COUNT - 3) + 3;
            SortedBag.Builder<Ticket> sbb = new SortedBag.Builder<>();

            for (int i = 0; i < chosenTicketsCount; ++i)
            {
                int chosenTicket = new Random().nextInt(initialTicketChoice.size());
                sbb.add(initialTicketChoice.get(chosenTicket));
                initialTicketChoice = initialTicketChoice.difference(SortedBag.of(initialTicketChoice.get(chosenTicket)));
            }

            expectedInfoQueue.offer(info.keptTickets(chosenTicketsCount));

            return sbb.build();
        }
        @Override
        public TurnKind nextTurn()
        {
            ++turnCount;
            if (turnCount > TURN_LIMIT)
                throw new Error("Too many turns");

            List<Route> claimableRoutes =
                    ChMap.routes().stream()
                            .filter(route -> playerState.canClaimRoute(route) && !gameState.claimedRoutes().contains(route))
                            .collect(Collectors.toList());

            if (!claimableRoutes.isEmpty())
            {
                claimedRoute = claimableRoutes.get(new Random().nextInt(claimableRoutes.size()));

                List<SortedBag<Card>> possibleClaimCards = playerState.possibleClaimCards(claimedRoute);
                claimCards = possibleClaimCards.get(new Random().nextInt(possibleClaimCards.size()));

                if (claimedRoute.level() == Route.Level.OVERGROUND)
                    expectedInfoQueue.offer(info.claimedRoute(claimedRoute, claimCards));
                else
                    expectedInfoQueue.offer(info.attemptsTunnelClaim(claimedRoute, claimCards));

                return TurnKind.CLAIM_ROUTE;
            }


            int action = new Random().nextInt(10);

            if (gameState.canDrawTickets() && action == 0)
                return TurnKind.DRAW_TICKETS;

            if (gameState.cardState().discardsSize() + gameState.cardState().deckSize() > 5)
                return TurnKind.DRAW_CARDS;
            else
                throw new NoPossibleAction();
        }
        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options)
        {
            int chosenTicketsCount = new Random().nextInt(options.size() - 1) + 1;
            SortedBag.Builder<Ticket> sbb = new SortedBag.Builder<>();

            for (int i = 0; i < chosenTicketsCount; ++i)
            {
                int chosenTicket = new Random().nextInt(options.size());
                sbb.add(options.get(chosenTicket));
                options = options.difference(SortedBag.of(options.get(chosenTicket)));
            }

//		expectedInfoQueue.offer(info.drewTickets(chosenTicketsCount));

            return sbb.build();
        }
        @Override
        public int drawSlot()
        {
            int chosenSlot = new Random().nextInt(6) - 1;

            if (chosenSlot == -1)
                expectedInfoQueue.offer(info.drewBlindCard());
            else
                expectedInfoQueue.offer(info.drewVisibleCard(gameState.cardState().faceUpCard(chosenSlot)));

            return chosenSlot;
        }
        @Override
        public Route claimedRoute()
        {
            return claimedRoute;
        }
        @Override
        public SortedBag<Card> initialClaimCards()
        {
            return claimCards;
        }
        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options)
        {
            List<SortedBag<Card>> playableOptions =
                    options.stream()
                            .filter(sb -> playerState.cards().contains(sb))
                            .collect(Collectors.toList());

            if (playableOptions.isEmpty())
            {
                expectedInfoQueue.offer(info.didNotClaimRoute(claimedRoute));
                return SortedBag.of();
            }
            else
            {
                SortedBag<Card> chosenOption = playableOptions.get(new Random().nextInt(playableOptions.size()));
                expectedInfoQueue.offer(info.claimedRoute(claimedRoute, claimCards.union(chosenOption)));
                return chosenOption;
            }
        }
    }


    public static class RandomPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        public int turnCounter;
        protected PlayerState ownState;
        protected PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private SortedBag<Ticket> initialTickets;

        public RandomPlayer(long randomSeed, List<Route> allRoutes) {
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
            int size = newState.cardState().totalSize();
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
            if (gameState.canDrawTickets() && rng.nextFloat() < 0.1){
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


    public static class PlayerTest implements Player {
        private PlayerId ownId;
        private Map<PlayerId, String> playerNames;
        private final static Scanner scanner = new Scanner(System.in);
        private SortedBag<Ticket> chosenTickets;
        private PlayerState state;
        private Route actualRoute;
        private PublicGameState gameState;
        int turnCount=0;

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerNames = playerNames;
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(playerNames.get(ownId) + ": " + info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            gameState=newState;
            state = ownState;
            System.out.println("<>/*/*/*/*/*/*/<>STATE UPDATED<>/*/*/*/*/*/*/*/*/*/<>");
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println("Quelles tickets? (Au moins 3)");
            Ticket t1 = tickets.get(0);
            System.out.println("Ticket 1 " + t1.text());
            Ticket t2 = tickets.get(1);
            System.out.println("Ticket 2 " + t2.text());
            Ticket t3 = tickets.get(2);
            System.out.println("Ticket 3 " + t3.text());
            Ticket t4 = tickets.get(3);
            System.out.println("Ticket 4 " + t4.text());
            Ticket t5 = tickets.get(4);
            System.out.println("Ticket 5 " + t5.text());

            int d = scanner.nextInt();
            switch (d) {
                case 123:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).build();
                    break;
                case 124:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t4).build();
                    break;
                case 125:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t5).build();
                    break;
                case 134:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t4).build();
                    break;
                case 135:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t5).build();
                    break;
                case 145:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t4).add(t5).build();
                    break;
                case 234:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t4).build();
                    break;
                case 235:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t5).build();
                    break;
                case 245:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t4).add(t5).build();
                    break;
                case 345:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t3).add(t4).add(t5).build();
                    break;
                case 1234:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t4).build();
                    break;
                case 1235:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t5).build();
                    break;
                case 1245:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t4).add(t5).build();
                    break;
                case 1345:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t3).add(t4).add(t5).build();
                    break;
                case 2345:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t2).add(t3).add(t4).add(t5).build();
                    break;
                case 12345:
                    chosenTickets = new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).add(t4).add(t5).build();
                    break;
            }

        }
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return chosenTickets;
        }

        @Override
        public TurnKind nextTurn() {
            turnCount++;
            System.out.println("C'est ton tour n°"+turnCount+"! :3");
            System.out.println(playerNames.get(ownId) + " a toi de jouer! \nQue veux tu faire pour ce tour ? \nc pour piocher , r pour prendre une route(ou au moins essayer ;) ) ou t pour ticket");
            System.out.println("Tu as "+state.carCount()+" wagons ! "+playerNames.get(ownId.next())+" en a quand à lui "+gameState.playerState(ownId.next()).carCount()+". ");
            System.out.println("Pour rappel tu tu possède ces cartes: " + state.cards().toString());
            System.out.println("Et tes tickets sont :");
            for (Ticket t :state.tickets()){
                System.out.print(t.text()+" ,");
            }
            System.out.println();
            if(state.routes().isEmpty()){
                System.out.println("Tu n'as pas encore de routes !");
            }else {
                System.out.println("Tes routes sont :");
                for (Route r : state.routes()) {
                    System.out.print(r.station1() + "-" + r.station2() + " ,");
                }
                System.out.println();
            }
            String s;
            do{
                s = scanner.next();
            }while (!s.equals("c")&&!s.equals("t")&&!s.equals("r"));

            switch (s) {
                case "c":
                    return TurnKind.DRAW_CARDS;
                case "t":
                    return TurnKind.DRAW_TICKETS;
                case "r":
                    return TurnKind.CLAIM_ROUTE;
            }
            return TurnKind.DRAW_CARDS;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.println("Quelles tickets? (Au moins 1)");
            Ticket t1 = options.get(0);
            System.out.println("Ticket 1" + t1.text());
            Ticket t2 = options.get(1);
            System.out.println("Ticket 2" + t2.text());
            Ticket t3 = options.get(2);
            System.out.println("Ticket 3" + t3.text());

            int d = scanner.nextInt();
            switch (d) {
                case 1:
                    return new SortedBag.Builder<Ticket>().add(t1).build();
                case 2:
                    return new SortedBag.Builder<Ticket>().add(t2).build();
                case 3:
                    return new SortedBag.Builder<Ticket>().add(t3).build();
                case 12:
                    return new SortedBag.Builder<Ticket>().add(t1).add(t2).build();
                case 13:
                    return new SortedBag.Builder<Ticket>().add(t1).add(t3).build();
                case 23:
                    return new SortedBag.Builder<Ticket>().add(t2).add(t3).build();
                case 123:
                    return new SortedBag.Builder<Ticket>().add(t1).add(t2).add(t3).build();
            }
            return null;
        }

        @Override
        public int drawSlot() {
            System.out.println("Les face up cards sont :");
            for (Card c: gameState.cardState().faceUpCards()) {
                System.out.println(c);
            }
            System.out.println();
            return scanner.nextInt();
        }

        @Override
        public Route claimedRoute() {
            System.out.println("Entrez ville depart: ");
            String s1 = scanner.next();
            System.out.println("Entrez ville d'arrivée: ");
            String s2 = scanner.next();
            List<Route> routes=new ArrayList<>();
            for (Route r : ChMap.routes()) {
                String r1 = r.station1().toString();
                String r2 = r.station2().toString();
                if ((r1.equals(s1)) && (r.station2().toString().equals(s2))&&state.canClaimRoute(r)) {
                    routes.add(r);
                }
                if ((r.station2().toString().equals(s1)) && (r.station1().toString().equals(s2))&&state.canClaimRoute(r)) {
                    routes.add(r);
                }

            }
            if (routes.size()==1) {
                actualRoute=routes.get(0);
                return routes.get(0);
            }

            System.out.println("quelle couleur? Entrez: ");
            System.out.println("0 pour "+routes.get(0).color());
            System.out.println("ou 1 pour"+routes.get(1).color());
            int s=scanner.nextInt();
            actualRoute=routes.get(s);
            return routes.get(s);
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            System.out.println("Quelles cartes voulez vous utiliser?");
            List<SortedBag<Card>> possibleCards = new ArrayList<>();
            System.out.println("choix possibles: ");
            for (int i = 0; i < state.possibleClaimCards(actualRoute).size(); i++) {
                System.out.println("Choix " + i + ": ");
                System.out.println(state.possibleClaimCards(actualRoute).get(i).toString());
                possibleCards.add(state.possibleClaimCards(actualRoute).get(i));
            }

            return possibleCards.get(scanner.nextInt());
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            if(options.isEmpty()){
                System.out.println("vous ne pouvez malheureusement pas prendre la route :( SADD BROooo:3");
                return SortedBag.of();
            }
            System.out.println("Pour rappel vos cartes sont:"+ state.cards().toString());
            System.out.println("Quelles cartes voulez vous utiliser?");
            List<SortedBag<Card>> possibleCards = new ArrayList<>();
            System.out.println("choix possibles: ");
            for (int i = 0; i < options.size(); i++) {
                System.out.println("Choix " + i + ": ");
                System.out.println(options.get(i).toString());
                possibleCards.add(options.get(i));
            }
            System.out.println("entrez -1 si vous ne souhaitez pas prendre la route");
            int i=scanner.nextInt();
            if(i==-1) return SortedBag.of();
            return possibleCards.get(i);

        }
    }
}
