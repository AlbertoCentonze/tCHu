package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    List<Station> from = List.of(
            new Station(0, "Lausanne"),
            new Station(1, "Neuchâtel"));
    List<Station> to = List.of(
            new Station(2, "Berne"),
            new Station(3, "Zürich"),
            new Station(4, "Coire"));
    int points = 17;

    int[][] expectedFromToIds = new int[][]{
            new int[]{0, 2},
            new int[]{0, 3},
            new int[]{0, 4},
            new int[]{1, 2},
            new int[]{1, 3},
            new int[]{1, 4},
    };
    List<Trip> all = Trip.all(from, to, points);

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
        List<SortedBag<Card>> possibleCards = player.possibleClaimCards(r);
        for(SortedBag<Card> s : possibleCards) {
            System.out.println(s.toString());
        } // empty
    }

    @Test
    void possibleClaimCardsWorksShortTunnel() {
        List<SortedBag<Card>> possibleCards = player.possibleClaimCards(shortTunnel);
        for(SortedBag<Card> s : possibleCards) {
            System.out.println(s.toString());
        } // LOCOMOTIVE & GREEN
    }

    @Test
    void possibleClaimCardsWorksNullTunnel() {
        PlayerState playerNullTunnel = new PlayerState(tickets, cardsForNullTunnel, suggestedTestRoutes);
        List<SortedBag<Card>> possibleCards = playerNullTunnel.possibleClaimCards(nullTunnel);
        for(SortedBag<Card> s : possibleCards) {
            System.out.println(s.toString());
        } // 4xORANGE, 3xYELLOW & 1xLOCOMOTIVE, 3xORANGE & 1xLOCOMOTIVE
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
        assertThrows(IllegalArgumentException.class, () -> playerFewWagons.canClaimRoute(r));
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
        assertEquals(suggestedTestRoutesWithAddedTunnel, player.withClaimedRoute(shortTunnel, SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE))).routes());
        assertEquals(cardsWithoutShortTunnelCards, player.withClaimedRoute(shortTunnel, SortedBag.of(List.of(Card.GREEN, Card.LOCOMOTIVE))).cards());
    }

}
