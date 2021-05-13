package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {
    private final Info i = new Info("Noah");

    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station NEU = new Station(19, "Neuchâtel");

    private static final Station BRI = new Station(4, "Brigue");
    private static final Station IT5 = new Station(46, "Italie");

    private final Route r = new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null);
    private final Route tunnel = new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN);

    private SortedBag<Card> cards = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE));
    private SortedBag<Card> cards2 = SortedBag.of(List.of(Card.WHITE, Card.BLUE, Card.WHITE));
    private SortedBag<Card> cardsTunnel = SortedBag.of(List.of(Card.LOCOMOTIVE, Card.GREEN, Card.GREEN));


    @Test
    void cardNameWorksWithOneCard() {
        assertEquals("noire", Info.cardName(Card.BLACK, 1));
    }

    //@Test
    void cardNameWorksWithOneCard2() {
        assertEquals("verte", Info.cardName(Card.GREEN, 1));
    }

    //@Test
    void cardNameWorksWithOneCard3() {
        assertEquals("locomotive", Info.cardName(Card.LOCOMOTIVE, 1));
    }

    @Test
    void cardNameWorksWithMultipleCards() {
        assertEquals("bleues", Info.cardName(Card.BLUE, 3));
    }

    @Test
    void drawWorks() { assertEquals("\nAlberto et Emma sont ex æqo avec 50 points !\n", Info.draw(List.of("Alberto", "Emma"),50)); }

    @Test
    void willPlayFirstWorks() { assertEquals("Noah jouera en premier.\n\n", i.willPlayFirst()); }

    @Test
    void keptTicketsWorksMultiple() { assertEquals("Noah a gardé 3 billets.\n", i.keptTickets(3)); }

    @Test
    void keptTicketsWorksSingle() { assertEquals("Noah a gardé 1 billet.\n", i.keptTickets(1)); }

    @Test
    void canPlayWorks() { assertEquals("\nC'est à Noah de jouer.\n", i.canPlay());}

    @Test
    void drewTicketsWorks() { assertEquals("Noah a tiré 3 billets...\n", i.drewTickets(3));}

    @Test
    void drewBlindCardWorks() { assertEquals("Noah a tiré une carte de la pioche.\n", i.drewBlindCard());}

    @Test
    void drewVisibleCardWorks() { assertEquals("Noah a tiré une carte rouge visible.\n", i.drewVisibleCard(Card.RED));}

    @Test
    void claimedRouteWorks() { Assertions.assertEquals("Noah a pris possession de la route Lausanne" + StringsFr.EN_DASH_SEPARATOR + "Neuchâtel au moyen de 1 verte, 2 oranges et 1 locomotive.\n", i.claimedRoute(r, cards));}

    @Test
    void attemptsTunnelClaimWorks() { assertEquals("Noah tente de s'emparer du tunnel Brigue" + StringsFr.EN_DASH_SEPARATOR + "Italie au moyen de 2 vertes et 1 locomotive !\n", i.attemptsTunnelClaim(tunnel, cardsTunnel));}

    @Test
    void drewAdditionalCardsWorksAdditional() { assertEquals("Les cartes supplémentaires sont 1 bleue et 2 blanches. " + "Elles impliquent un coût additionnel de 2 cartes.\n", i.drewAdditionalCards(cards2, 2));}

    @Test
    void drewAdditionalCardsWorksNOAdditional() { assertEquals("Les cartes supplémentaires sont 1 bleue et 2 blanches. " + "Elles n'impliquent aucun coût additionnel.\n", i.drewAdditionalCards(cards2, 0));}


    @Test
    void didNotClaimRouteWorks() { assertEquals("Noah n'a pas pu (ou voulu) s'emparer de la route Lausanne" + StringsFr.EN_DASH_SEPARATOR + "Neuchâtel.\n", i.didNotClaimRoute(r));}

    @Test
    void lastTurnBeginsWorks2Wagons() { assertEquals("\nNoah n'a plus que 2 wagons, le dernier tour commence !\n", i.lastTurnBegins(2) );}



    Station s1 = new Station(1, "Yverdon");
    Station s2 = new Station(2, "Fribourg");
    Station s3 = new Station(3, "Neuchâtel");
    Station s4 = new Station(4, "Berne");
    Station s5 = new Station(5, "Lucerne");
    Station s6 = new Station(6, "Soleure");

    List<Route> routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));
    @Test
    void didNotClaimRouteWorks3() { assertEquals("\nNoah reçoit un bonus de 10 points pour le plus long trajet (Lucerne" + StringsFr.EN_DASH_SEPARATOR + "Fribourg).\n", i.getsLongestTrailBonus(Trail.longest(routes)));}

    @Test
    void wonWorks() { assertEquals("\nNoah remporte la victoire avec 30 points, contre 1 point !\n", i.won(30,1));}

    private final Route r2 = new Route("LAU_NEU_2", LAU, NEU, 4, Route.Level.OVERGROUND, null);
    private SortedBag<Card> cards5 = SortedBag.of(List.of(Card.LOCOMOTIVE, Card.LOCOMOTIVE, Card.LOCOMOTIVE, Card.LOCOMOTIVE));

    @Test
    void claimedRouteWorksWithOneCard(){
        //System.out.println(i.claimedRoute(r2, cards5));
        assertEquals("Noah a pris possession de la route Lausanne" + StringsFr.EN_DASH_SEPARATOR +
                "Neuchâtel au moyen de 4 locomotives.\n", i.claimedRoute(r2, cards5));

    }

    // -------------------------------------- MANDATORY TESTS ---------------------------------------------------

    @Test
    void infoCardNameWorks() {
        var actualK1 = Info.cardName(Card.BLACK, 1);
        var expectedK1 = "noire";
        assertEquals(expectedK1, actualK1);
        var actualK9 = Info.cardName(Card.BLACK, 9);
        var expectedK9 = "noires";
        assertEquals(expectedK9, actualK9);

        var actualB1 = Info.cardName(Card.BLUE, 1);
        var expectedB1 = "bleue";
        assertEquals(expectedB1, actualB1);
        var actualB9 = Info.cardName(Card.BLUE, 9);
        var expectedB9 = "bleues";
        assertEquals(expectedB9, actualB9);

        var actualG1 = Info.cardName(Card.GREEN, 1);
        var expectedG1 = "verte";
        assertEquals(expectedG1, actualG1);
        var actualG9 = Info.cardName(Card.GREEN, 9);
        var expectedG9 = "vertes";
        assertEquals(expectedG9, actualG9);

        var actualO1 = Info.cardName(Card.ORANGE, 1);
        var expectedO1 = "orange";
        assertEquals(expectedO1, actualO1);
        var actualO9 = Info.cardName(Card.ORANGE, 9);
        var expectedO9 = "oranges";
        assertEquals(expectedO9, actualO9);

        var actualR1 = Info.cardName(Card.RED, 1);
        var expectedR1 = "rouge";
        assertEquals(expectedR1, actualR1);
        var actualR9 = Info.cardName(Card.RED, 9);
        var expectedR9 = "rouges";
        assertEquals(expectedR9, actualR9);

        var actualV1 = Info.cardName(Card.VIOLET, 1);
        var expectedV1 = "violette";
        assertEquals(expectedV1, actualV1);
        var actualV9 = Info.cardName(Card.VIOLET, 9);
        var expectedV9 = "violettes";
        assertEquals(expectedV9, actualV9);

        var actualW1 = Info.cardName(Card.WHITE, 1);
        var expectedW1 = "blanche";
        assertEquals(expectedW1, actualW1);
        var actualW9 = Info.cardName(Card.WHITE, 9);
        var expectedW9 = "blanches";
        assertEquals(expectedW9, actualW9);

        var actualY1 = Info.cardName(Card.YELLOW, 1);
        var expectedY1 = "jaune";
        assertEquals(expectedY1, actualY1);
        var actualY9 = Info.cardName(Card.YELLOW, 9);
        var expectedY9 = "jaunes";
        assertEquals(expectedY9, actualY9);

        var actualL1 = Info.cardName(Card.LOCOMOTIVE, 1);
        var expectedL1 = "locomotive";
        assertEquals(expectedL1, actualL1);
        var actualL9 = Info.cardName(Card.LOCOMOTIVE, 9);
        var expectedL9 = "locomotives";
        assertEquals(expectedL9, actualL9);
    }

    @Test
    void infoDrawWorks() {
        var actual = Info.draw(List.of("Ada", "Ada"), 17);
        var expected = "\nAda et Ada sont ex æqo avec 17 points !\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoWillPlayFirstWorks() {
        var info = new Info("Niklaus");
        var actual = info.willPlayFirst();
        var expected = "Niklaus jouera en premier.\n\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoKeptTicketsWorks() {
        var info = new Info("Edsger");

        var actual1 = info.keptTickets(1);
        var expected1 = "Edsger a gardé 1 billet.\n";
        assertEquals(expected1, actual1);

        var actual5 = info.keptTickets(5);
        var expected5 = "Edsger a gardé 5 billets.\n";
        assertEquals(expected5, actual5);
    }

    @Test
    void infoCanPlayWorks() {
        var info = new Info("Charles");

        var actual = info.canPlay();
        var expected = "\nC'est à Charles de jouer.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoDrewTicketsWorks() {
        var info = new Info("Linus");

        var actual1 = info.drewTickets(1);
        var expected1 = "Linus a tiré 1 billet...\n";
        assertEquals(expected1, actual1);

        var actual5 = info.drewTickets(5);
        var expected5 = "Linus a tiré 5 billets...\n";
        assertEquals(expected5, actual5);
    }

    @Test
    void infoDrewBlindCardWorks() {
        var info = new Info("Alan");

        var actual = info.drewBlindCard();
        var expected = "Alan a tiré une carte de la pioche.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoDrewVisibleCardWorks() {
        var info = new Info("John");

        var actual = info.drewVisibleCard(Card.GREEN);
        var expected = "John a tiré une carte verte visible.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoClaimedRouteWorks() {
        var info = new Info("Brian");

        var s1 = new Station(0, "Neuchâtel");
        var s2 = new Station(1, "Lausanne");

        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.ORANGE);
        var actual1 = info.claimedRoute(route1, SortedBag.of(Card.ORANGE));
        var expected1 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 1 orange.\n";
        assertEquals(expected1, actual1);

        var route2 = new Route("1", s1, s2, 2, Route.Level.OVERGROUND, null);
        var actual2 = info.claimedRoute(route2, SortedBag.of(2, Card.RED));
        var expected2 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 2 rouges.\n";
        assertEquals(expected2, actual2);

        var route3 = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, null);
        var actual3 = info.claimedRoute(route3, SortedBag.of(4, Card.BLUE, 2, Card.LOCOMOTIVE));
        var expected3 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 4 bleues et 2 locomotives.\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoAttemptsTunnelClaimWorks() {
        var info = new Info("Grace");

        var s1 = new Station(0, "Wassen");
        var s2 = new Station(1, "Coire");

        var route1 = new Route("1", s1, s2, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        var actual1 = info.attemptsTunnelClaim(route1, SortedBag.of(Card.ORANGE));
        var expected1 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 1 orange !\n";
        assertEquals(expected1, actual1);

        var route2 = new Route("1", s1, s2, 2, Route.Level.UNDERGROUND, null);
        var actual2 = info.attemptsTunnelClaim(route2, SortedBag.of(2, Card.RED));
        var expected2 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 2 rouges !\n";
        assertEquals(expected2, actual2);

        var route3 = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, null);
        var actual3 = info.attemptsTunnelClaim(route3, SortedBag.of(4, Card.BLUE, 2, Card.LOCOMOTIVE));
        var expected3 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 4 bleues et 2 locomotives !\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoDrewAdditionalCardsWorks() {
        var info = new Info("Margaret");

        var actual1 = info.drewAdditionalCards(SortedBag.of(3, Card.ORANGE), 0);
        var expected1 = "Les cartes supplémentaires sont 3 oranges. Elles n'impliquent aucun coût additionnel.\n";
        assertEquals(expected1, actual1);

        var actual2 = info.drewAdditionalCards(SortedBag.of(1, Card.WHITE, 2, Card.RED), 1);
        var expected2 = "Les cartes supplémentaires sont 2 rouges et 1 blanche. Elles impliquent un coût additionnel de 1 carte.\n";
        assertEquals(expected2, actual2);

        var actual3 = info.drewAdditionalCards(SortedBag.of(1, Card.YELLOW, 2, Card.GREEN), 2);
        var expected3 = "Les cartes supplémentaires sont 2 vertes et 1 jaune. Elles impliquent un coût additionnel de 2 cartes.\n";
        assertEquals(expected3, actual3);

        var actual4 = info.drewAdditionalCards(SortedBag.of(1, Card.VIOLET, 2, Card.LOCOMOTIVE), 3);
        var expected4 = "Les cartes supplémentaires sont 1 violette et 2 locomotives. Elles impliquent un coût additionnel de 3 cartes.\n";
        assertEquals(expected4, actual4);
    }

    @Test
    void infoDidNotClaimRouteWorks() {
        var info = new Info("Guido");
        var s1 = new Station(0, "Zernez");
        var s2 = new Station(1, "Klosters");

        var route = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        var actual = info.didNotClaimRoute(route);
        var expected = "Guido n'a pas pu (ou voulu) s'emparer de la route Zernez – Klosters.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoLastTurnBeginsWorks() {
        var info = new Info("Martin");

        var actual1 = info.lastTurnBegins(0);
        var expected1 = "\nMartin n'a plus que 0 wagon, le dernier tour commence !\n";
        assertEquals(expected1, actual1);

        var actual2 = info.lastTurnBegins(1);
        var expected2 = "\nMartin n'a plus que 1 wagon, le dernier tour commence !\n";
        assertEquals(expected2, actual2);

        var actual3 = info.lastTurnBegins(2);
        var expected3 = "\nMartin n'a plus que 2 wagons, le dernier tour commence !\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoGetsLongestTrailBonusWorks() {
        var info = new Info("Larry");

        var s1 = new Station(0, "Montreux");
        var s2 = new Station(1, "Montreux");

        var route = new Route("1", s1, s2, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        var trail = Trail.longest(List.of(route));

        var actual = info.getsLongestTrailBonus(trail);
        var expected = "\nLarry reçoit un bonus de 10 points pour le plus long trajet (Montreux – Montreux).\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoWonWorks() {
        var info = new Info("Bjarne");

        var actual1 = info.won(2, 1);
        var expected1 = "\nBjarne remporte la victoire avec 2 points, contre 1 point !\n";
        assertEquals(expected1, actual1);

        var actual2 = info.won(3, 2);
        var expected2 = "\nBjarne remporte la victoire avec 3 points, contre 2 points !\n";
        assertEquals(expected2, actual2);
    } }