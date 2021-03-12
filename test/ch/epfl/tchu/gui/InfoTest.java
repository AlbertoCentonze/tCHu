package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
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
    void claimedRouteWorks() { assertEquals("Noah a pris possession de la route Lausanne" + StringsFr.EN_DASH_SEPARATOR + "Neuchâtel au moyen de 1 verte, 2 oranges et 1 locomotive.\n", i.claimedRoute(r, cards));}

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
        System.out.println(i.claimedRoute(r2, cards5));
        assertEquals("Noah a pris possession de la route Lausanne" + StringsFr.EN_DASH_SEPARATOR +
                "Neuchâtel au moyen de 4 locomotives.\n", i.claimedRoute(r2, cards5));

    }


    @Test
    void cardNameTest(){
        System.out.println(Info.cardName(Card.BLUE,1));
        System.out.println(Info.cardName(Card.BLUE,2));
    }

    @Test
    void drawnTest(){
        List <String> pl = new ArrayList<>();
        pl.add("Frank");
        pl.add("Rémi");
        pl.add("Alice");
        System.out.println(Info.draw(pl,12));

    }
    @Test
    void infoTest(){
        Info inPl = new Info("Frank");
        System.out.println(inPl.canPlay());
        System.out.println(inPl.drewBlindCard());
        System.out.println(inPl.willPlayFirst());
        System.out.println(inPl.drewTickets(3));
        System.out.println(inPl.drewTickets(1));
        System.out.println(inPl.keptTickets(1));
        System.out.println(inPl.keptTickets(2));
        System.out.println(inPl.lastTurnBegins(1));
        System.out.println(inPl.lastTurnBegins(2));
        System.out.println(inPl.won(1,0));
        System.out.println(inPl.won(10,1));
    }

}
