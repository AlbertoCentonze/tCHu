package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



import static ch.epfl.tchu.game.ChMap.routes;
import static ch.epfl.tchu.game.ChMap.tickets;



import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station NEU = new Station(19, "Neuchâtel");
    private final Route r = new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null);

    private final Route shortRoute = new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE);
    private final Route shortTunnel = getRoutesListFromIds(new ArrayList<>(Arrays.asList("BRU_IT2_1"))).get(0);
            // new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN);
    private final Route nullTunnel = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);

    private final List<Route> suggestedTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1")));
    private final List<Route> suggestedTestRoutesWithAddedTunnel = getRoutesListFromIds(new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1", "BRU_IT2_1")));

    private final List<Route> shorterTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));
    private final List<Route> foreignTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("FR3_LCF_1", "DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));
    private final List<Route> longerTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("DEL_SOL_1", "NEU_YVE_1", "BER_NEU_1", "LCF_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "DEL_LCF_1", "NEU_SOL_1")));

    private static List<Route> getRoutesListFromIds(List<String> ids){
        List<Route> routes = ChMap.routes();
        return routes.stream().filter(route -> {
            for (String id : ids) {
                if (route.id().equals(id)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private SortedBag<Ticket> tickets = SortedBag.of(List.of(new Ticket(BAD, BAL, 5), new Ticket(LAU, FR1, 7), new Ticket(YVE, WAS, 6)));

    private SortedBag<Card> cards = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE, Card.YELLOW, Card.YELLOW, Card.VIOLET, Card.RED));
    private SortedBag<Card> cardsWithAddedCard= SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE, Card.YELLOW, Card.YELLOW, Card.VIOLET, Card.RED, Card.RED));
    private SortedBag<Card> cardsInit = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE));
    private SortedBag<Card> cardsInitWithAddedCards = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE, Card.YELLOW, Card.BLUE, Card.VIOLET));
    private SortedBag<Card> additionalCards = SortedBag.of(List.of(Card.YELLOW, Card.BLUE, Card.VIOLET));
    private SortedBag<Card> cardsForNullTunnel = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE, Card.YELLOW, Card.YELLOW, Card.ORANGE, Card.ORANGE, Card.YELLOW, Card.RED));

    private SortedBag<Card> cardsWithoutShortTunnelCards = SortedBag.of(List.of(Card.ORANGE, Card.ORANGE, Card.YELLOW, Card.YELLOW, Card.VIOLET, Card.RED));


    private PlayerState playerInit = PlayerState.initial(cardsInit);
    private PlayerState player = new PlayerState(tickets, cards, suggestedTestRoutes);


    @Test
    void initialWorks() {
        assertEquals(0, playerInit.ticketCount());
        assertEquals(Collections.emptyList(), playerInit.routes());
    }

    @Test
    void initialFailsWithMoreThan4Cards() {
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(cards));
    }

    @Test
    void initialFailsWithLessThan4Cards() {
        assertThrows(IllegalArgumentException.class, () -> PlayerState.initial(SortedBag.of()));
    }

    @Test
    void ticketsWorks() {
        assertEquals(tickets, player.tickets());
        assertEquals(SortedBag.of(), playerInit.tickets());
    }


    @Test
    void cardsWorks() {
        assertEquals(cards, player.cards());
        assertEquals(cardsInit, playerInit.cards());
    }

    @Test
    void withAddedCardWorks() {
        assertEquals(cardsWithAddedCard, player.withAddedCard(Card.RED).cards());
    }

    @Test
    void withAddedCardsWorks() {
        assertEquals(cardsInitWithAddedCards, playerInit.withAddedCards(additionalCards).cards());
    }

    @Test
    void possibleClaimCardsWorks() {
        assertEquals(Collections.emptyList(), player.possibleClaimCards(r));
    }

    @Test
    void possibleClaimCardsWorksShortTunnel() {
        assertEquals(List.of(SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.GREEN)), player.possibleClaimCards(shortTunnel));
    }

    @Test
    void possibleClaimCardsWorksNullTunnel() {
        PlayerState playerNullTunnel = new PlayerState(tickets, cardsForNullTunnel, suggestedTestRoutes);
        assertEquals(List.of(SortedBag.of(4, Card.ORANGE), SortedBag.of(3, Card.YELLOW, 1, Card.LOCOMOTIVE), SortedBag.of(3, Card.ORANGE, 1, Card.LOCOMOTIVE)), playerNullTunnel.possibleClaimCards(nullTunnel));
    }


    private static final Station BAD = new Station(0, "Baden");
    private static final Station BAL = new Station(1, "Bâle");
    private static final Station BEL = new Station(2, "Bellinzone");
    private static final Station BER = new Station(3, "Berne");
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station BRU = new Station(5, "Brusio");
    private static final Station COI = new Station(6, "Coire");
    private static final Station DAV = new Station(7, "Davos");
    private static final Station DEL = new Station(8, "Delémont");
    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station GEN = new Station(10, "Genève");
    private static final Station INT = new Station(11, "Interlaken");
    private static final Station KRE = new Station(12, "Kreuzlingen");
    // private static final Station LAU = new Station(13, "Lausanne");
    private static final Station LCF = new Station(14, "La Chaux-de-Fonds");
    private static final Station LOC = new Station(15, "Locarno");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station LUG = new Station(17, "Lugano");
    private static final Station MAR = new Station(18, "Martigny");
    // private static final Station NEU = new Station(19, "Neuchâtel");
    private static final Station OLT = new Station(20, "Olten");
    private static final Station PFA = new Station(21, "Pfäffikon");
    private static final Station SAR = new Station(22, "Sargans");
    private static final Station SCE = new Station(23, "Schaffhouse");
    private static final Station SCZ = new Station(24, "Schwyz");
    private static final Station SIO = new Station(25, "Sion");
    private static final Station SOL = new Station(26, "Soleure");
    private static final Station STG = new Station(27, "Saint-Gall");
    private static final Station VAD = new Station(28, "Vaduz");
    private static final Station WAS = new Station(29, "Wassen");
    private static final Station WIN = new Station(30, "Winterthour");
    private static final Station YVE = new Station(31, "Yverdon");
    private static final Station ZOU = new Station(32, "Zoug");
    private static final Station ZUR = new Station(33, "Zürich");

    // Stations - countries
    private static final Station DE1 = new Station(34, "Allemagne");
    private static final Station DE2 = new Station(35, "Allemagne");
    private static final Station DE3 = new Station(36, "Allemagne");
    private static final Station DE4 = new Station(37, "Allemagne");
    private static final Station DE5 = new Station(38, "Allemagne");
    private static final Station AT1 = new Station(39, "Autriche");
    private static final Station AT2 = new Station(40, "Autriche");
    private static final Station AT3 = new Station(41, "Autriche");
    private static final Station IT1 = new Station(42, "Italie");
    private static final Station IT2 = new Station(43, "Italie");
    private static final Station IT3 = new Station(44, "Italie");
    private static final Station IT4 = new Station(45, "Italie");
    private static final Station IT5 = new Station(46, "Italie");
    private static final Station FR1 = new Station(47, "France");
    private static final Station FR2 = new Station(48, "France");
    private static final Station FR3 = new Station(49, "France");
    private static final Station FR4 = new Station(50, "France");


    private final List<Route> manyRoutes = List.of(
            new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null),
            new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("BEL_LOC_1", BEL, LOC, 1, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BEL_LUG_2", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("BER_FRI_2", BER, FRI, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE),
            new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_LUC_2", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null),
            new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED),
            new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null),
            new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET));

    PlayerState playerFewWagons = new PlayerState(tickets, cards, manyRoutes);

    @Test
    void possibleClaimCardsFailsNotEnoughWagons() {
        assertThrows(IllegalArgumentException.class, () -> playerFewWagons.possibleClaimCards(r));
    }

    @Test
    void canClaimRouteFailsNotEnoughWagons() {
        assertFalse(playerFewWagons.canClaimRoute(r));
    }

    @Test
    void canClaimRouteWorksTrue() {
        assertTrue(player.canClaimRoute(shortRoute));
    }

    @Test
    void canClaimRouteWorksTrueTunnel() {
        assertTrue(player.canClaimRoute(shortTunnel));
    }

    @Test
    void canClaimRouteWorksFalse() {
        assertFalse(player.canClaimRoute(r));
    }

    @Test
    void withClaimedRouteWorks() {
        PlayerState tempPlayer = player.withClaimedRoute(shortTunnel, SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE)));
        for(Route r : tempPlayer.routes()) {
            assertTrue(suggestedTestRoutesWithAddedTunnel.contains(r));
        }
        assertEquals(cardsWithoutShortTunnelCards, player.withClaimedRoute(shortTunnel, SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE))).cards());
    }

    @Test
    void possibleAdditionalClaimCardsFails() {
        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.GREEN, Card.RED, Card.WHITE));
        // negative additionalCardsCount
        assertThrows(IllegalArgumentException.class, () -> player.possibleAdditionalCards(-1, initialCards, drawnCards));
        // additionalCardsCount > 3
        assertThrows(IllegalArgumentException.class, () -> player.possibleAdditionalCards(4, initialCards, drawnCards));
        // empty initial cards
        assertThrows(IllegalArgumentException.class, () -> player.possibleAdditionalCards(2, SortedBag.of(), drawnCards));
        // more than 2 types of Cards in initial cards
        assertThrows(IllegalArgumentException.class, () -> player.possibleAdditionalCards(2, cardsInit, drawnCards));
        // drawnCards.size() > 3
        assertThrows(IllegalArgumentException.class, () -> player.possibleAdditionalCards(2, initialCards, cardsInit));
    }

    @Test
    void possibleAdditionalClaimCardsWorksNoCardsRemaining() {
        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.GREEN, Card.RED, Card.WHITE));
        assertEquals(Collections.emptyList(), player.possibleAdditionalCards(1, initialCards, drawnCards));
    }

    @Test
    void possibleAdditionalClaimCardsWorks() {
        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.GREEN));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.GREEN, Card.RED, Card.WHITE));
        assertEquals(List.of(SortedBag.of(Card.LOCOMOTIVE)), player.possibleAdditionalCards(1, initialCards, drawnCards));
    }

    @Test
    void possibleAdditionalClaimCardsWorksTunnel() {
        PlayerState playerTunnel = new PlayerState(tickets, cardsForNullTunnel.union(SortedBag.of(Card.LOCOMOTIVE)), suggestedTestRoutes);
        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.ORANGE));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.ORANGE, Card.ORANGE, Card.WHITE)); // TODO additionalCardsCount corresponds to drawnCards ?
        assertEquals(List.of(SortedBag.of(2, Card.ORANGE), SortedBag.of(1, Card.ORANGE, 1, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE)), playerTunnel.possibleAdditionalCards(2, initialCards, drawnCards));
    }

    @Test
    void possibleAdditionalClaimCardsWorksOnlyLocomotives() {
        PlayerState playerTunnel = new PlayerState(tickets, cardsForNullTunnel.union(SortedBag.of(Card.LOCOMOTIVE)), suggestedTestRoutes);
        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.LOCOMOTIVE));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.ORANGE, Card.LOCOMOTIVE, Card.WHITE));
        assertEquals(List.of(SortedBag.of(1, Card.LOCOMOTIVE)), playerTunnel.possibleAdditionalCards(1, initialCards, drawnCards));
    }





    @Test
    void ErrorCheck(){

        SortedBag<Card> initialCards = SortedBag.of(5, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> { PlayerState.initial(initialCards); });

        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(3), 2, tickets().get(3));
        List<Route> routes = routes().subList(0, 10);
        var player2 = new PlayerState(tickets, initialCards, routes);

    }

    @Test
    void possibleAdditionalCardsNormalTest(){

        List<SortedBag<Card>> possibleCardsExpected = List.of(SortedBag.of(2, Card.GREEN),
                SortedBag.of(1, Card.GREEN, 1, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE));

        SortedBag<Card> cards1 = SortedBag.of(2, Card.LOCOMOTIVE, 2, Card.BLUE);
        SortedBag<Card> cards2 = SortedBag.of(3, Card.GREEN, 2, Card.RED);
        SortedBag<Card> cards = cards1.union(cards2);
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(3), 2, tickets().get(3));
        List<Route> routes = routes().subList(0, 10);
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(possibleCardsExpected, player.possibleAdditionalCards(2, SortedBag.of(1, Card.GREEN),
                SortedBag.of(2, Card.GREEN, 1, Card.RED)));
    }

    @Test
    void possibleAdditionalCardsWithNotEnoughCardsTest(){

        List<SortedBag<Card>> possibleCardsExpected = List.of();

        SortedBag<Card> cards1 = SortedBag.of(2, Card.BLUE);
        SortedBag<Card> cards2 = SortedBag.of(3, Card.GREEN, 2, Card.RED);
        SortedBag<Card> cards = cards1.union(cards2);
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(3), 2, tickets().get(3));
        List<Route> routes = routes().subList(0, 10);
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(possibleCardsExpected, player.possibleAdditionalCards(1, SortedBag.of(2, Card.RED),
                SortedBag.of(2, Card.GREEN, 1, Card.RED)));
    }

    @Test
    void possibleClaimCardsNormalTest(){

        List<SortedBag<Card>> possibleCardsExpected = List.of(SortedBag.of(1, Card.YELLOW));

        SortedBag<Card> cards1 = SortedBag.of(2, Card.LOCOMOTIVE, 2, Card.YELLOW);
        SortedBag<Card> cards2 = SortedBag.of(3, Card.GREEN, 2, Card.RED);
        SortedBag<Card> cards = cards1.union(cards2);
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(3), 2, tickets().get(3));
        List<Route> routes = routes().subList(0, 2);
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(possibleCardsExpected, player.possibleClaimCards(routes().get(14)));
    }

    @Test
    void possibleClaimCardsOnTunnelsTest(){

        List<SortedBag<Card>> possibleCardsExpected = List.of(SortedBag.of(2, Card.YELLOW),
                SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.YELLOW), SortedBag.of(2, Card.LOCOMOTIVE));

        SortedBag<Card> cards1 = SortedBag.of(2, Card.LOCOMOTIVE, 2, Card.YELLOW);
        SortedBag<Card> cards2 = SortedBag.of(3, Card.GREEN, 2, Card.RED);
        SortedBag<Card> cards = cards1.union(cards2);
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(3), 2, tickets().get(3));
        List<Route> routes = routes().subList(0, 2);
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(possibleCardsExpected, player.possibleClaimCards(routes().get(6)));
    }

    @Test
    void ticketPointsNormalTest(){

        int pointsExpected = 5;

        SortedBag<Card> cards = SortedBag.of();
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(0));
        List<Route> routes = List.of(routes().get(19), routes().get(40), routes().get(6));
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(pointsExpected, player.ticketPoints());
    }

    @Test
    void ticketPointsWithNoTicketTest(){

        int pointsExpected = 0;

        SortedBag<Card> cards = SortedBag.of();
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = List.of(routes().get(19), routes().get(40), routes().get(6));
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(pointsExpected, player.ticketPoints());
    }

    @Test
    void ticketPointsWithNegativePointsTest(){

        int pointsExpected = -5;

        SortedBag<Card> cards = SortedBag.of();
        SortedBag<Ticket> tickets = SortedBag.of(1, tickets().get(0));
        List<Route> routes = List.of(routes().get(19), routes().get(6));
        var player = new PlayerState(tickets, cards, routes);

        assertEquals(pointsExpected, player.ticketPoints());
    }


}
