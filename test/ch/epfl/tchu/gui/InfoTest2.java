package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class InfoTest2 extends TrailTest {

    final Station BAD = new Station(0, "Baden");
    final Station BAL = new Station(1, "Bâle");
    final Station BEL = new Station(2, "Bellinzone");
    final Station BER = new Station(3, "Berne");
    final Station BRI = new Station(4, "Brigue");
    final Station BRU = new Station(5, "Brusio");
    final Station COI = new Station(6, "Coire");
    final Station DAV = new Station(7, "Davos");
    final Station DEL = new Station(8, "Delémont");
    final Station FRI = new Station(9, "Fribourg");
    final Station GEN = new Station(10, "Genève");
    final Station INT = new Station(11, "Interlaken");
    final Station KRE = new Station(12, "Kreuzlingen");
    final Station LAU = new Station(13, "Lausanne");
    final Station LCF = new Station(14, "La Chaux-de-Fonds");
    final Station LOC = new Station(15, "Locarno");
    final Station LUC = new Station(16, "Lucerne");
    final Station LUG = new Station(17, "Lugano");
    final Station MAR = new Station(18, "Martigny");
    final Station NEU = new Station(19, "Neuchâtel");
    final Station OLT = new Station(20, "Olten");
    final Station PFA = new Station(21, "Pfäffikon");
    final Station SAR = new Station(22, "Sargans");
    final Station SCE = new Station(23, "Schaffhouse");
    final Station SCZ = new Station(24, "Schwyz");
    final Station SIO = new Station(25, "Sion");
    final Station SOL = new Station(26, "Soleure");
    final Station STG = new Station(27, "Saint-Gall");
    final Station VAD = new Station(28, "Vaduz");
    final Station WAS = new Station(29, "Wassen");
    final Station WIN = new Station(30, "Winterthour");
    final Station YVE = new Station(31, "Yverdon");
    final Station ZOU = new Station(32, "Zoug");
    final Station ZUR = new Station(33, "Zürich");
    final Station DE1 = new Station(34, "Allemagne");
    final Station DE2 = new Station(35, "Allemagne");
    final Station DE3 = new Station(36, "Allemagne");
    final Station DE4 = new Station(37, "Allemagne");
    final Station DE5 = new Station(38, "Allemagne");
    final Station AT1 = new Station(39, "Autriche");
    final Station AT2 = new Station(40, "Autriche");
    final Station AT3 = new Station(41, "Autriche");
    final Station IT1 = new Station(42, "Italie");
    final Station IT2 = new Station(43, "Italie");
    final Station IT3 = new Station(44, "Italie");
    final Station IT4 = new Station(45, "Italie");
    final Station IT5 = new Station(46, "Italie");
    final Station FR1 = new Station(47, "France");
    final Station FR2 = new Station(48, "France");
    final Station FR3 = new Station(49, "France");
    final Station FR4 = new Station(50, "France");

    final Route BER_LUC_1 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);
    final Route IT3_LUG_1 = new Route("IT3_LUG_1", IT3, LUG, 2, Route.Level.UNDERGROUND, Color.WHITE);
    final Route SAR_VAD_1 = new Route("SAR_VAD_1", SAR, VAD, 1, Route.Level.UNDERGROUND, Color.ORANGE);
    final Route GEN_YVE_1 = new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null);

    @Test
    void bonus(){
        // var chRoutes = new TrailTest.ChRoutes();
        Info t = new Info("Frank");
        var routes = List.of(
                SAR_VAD_1,
                BER_LUC_1,
                GEN_YVE_1,
                IT3_LUG_1);
        System.out.println(t.getsLongestTrailBonus(Trail.longest(routes)));
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

    final Route AT1_STG_1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
    final Route BAD_BAL_1 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
    final Route BRI_SIO_1 = new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK);

    @Test
    void infoTest2(){
        Info inPl = new Info("Frank");
        //var routes = new ChRoutes();
        SortedBag<Card> cards = SortedBag.of(3, Card.BLACK, 2, Card.LOCOMOTIVE);
        SortedBag<Card> addCards = SortedBag.of(1, Card.BLACK, 2, Card.LOCOMOTIVE);

        System.out.println(inPl.attemptsTunnelClaim(AT1_STG_1, cards));
        System.out.println(inPl.claimedRoute(BAD_BAL_1,cards));
        System.out.println(inPl.didNotClaimRoute(BRI_SIO_1));
        System.out.println(inPl.drewAdditionalCards(addCards, 2));
        System.out.println(inPl.drewAdditionalCards(addCards, 0));
        System.out.println(inPl.drewVisibleCard(Card.RED));

    }

}



