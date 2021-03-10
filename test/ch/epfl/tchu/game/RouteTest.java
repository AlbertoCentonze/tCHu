package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.tchu.SortedBag.*;
import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {
    private final Station SCZ = new Station(24, "Schwyz");
    private final Station SIO = new Station(25, "Sion");
    private final Station SOL = new Station(26, "Soleure");
    private final Route testRoute = new Route("AT1_STG_1", SIO, SCZ, 3, Route.Level.UNDERGROUND, null);
    private final Station FR3 = new Station(49, "France");
    private final Station LCF = new Station(14, "La Chaux-de-Fonds");

    @Test
    void constructorFailsOutOfRange(){
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("AT1_STG_1", SCZ, SIO, 100000000, Route.Level.UNDERGROUND, null);
        });
    }

    @Test
    void constructorFailsSameStation(){
        assertThrows(IllegalArgumentException.class, ()->{
            new Route("AT1_STG_1", SCZ, SCZ, 3, Route.Level.UNDERGROUND, null);
        });
    }

    @Test
    void constructorFailsNullStation(){
        assertThrows(NullPointerException.class, ()->{
            new Route("AT1_STG_1", null, SCZ, 3, Route.Level.UNDERGROUND, null);
        });
        assertThrows(NullPointerException.class, ()->{
            new Route("AT1_STG_1", SCZ, null, 3, Route.Level.UNDERGROUND, null);
        });
    }

    @Test
    void oppositeStationFailsPassingWrongStation(){
        assertThrows(IllegalArgumentException.class, ()->{
            Route r = new Route("AT1_STG_1", SIO, SCZ, 3, Route.Level.UNDERGROUND, null);
            r.stationOpposite(SOL);
        });
    }

    @Test
    void possibleClaimCardsWorksOnSurface(){
        List<SortedBag<Card>> cards = new Route("AT1_STG_1", SIO, SCZ, 3, Route.Level.OVERGROUND, null).possibleClaimCards();
        assertEquals("[{3×BLACK}, {3×VIOLET}, {3×BLUE}, {3×GREEN}, {3×YELLOW}, {3×ORANGE}, {3×RED}, {3×WHITE}]", cards.toString());
        assertEquals(8, cards.size());
        for (Route r : ChMap.routes().stream().filter((route) -> route.level() == Route.Level.OVERGROUND && route.color() == null).collect(Collectors.toList())){
            assertEquals(8, r.possibleClaimCards().size());
        }
        for (Route r : ChMap.routes().stream().filter((route) -> route.level() == Route.Level.OVERGROUND && route.color() != null).collect(Collectors.toList())){
            assertEquals(1, r.possibleClaimCards().size());
        }
    }

    @Test
    void possibleClaimCardsWorksWithExample(){
        List<SortedBag<Card>> cards = new Route("AT1_STG_1", SIO, SCZ, 2, Route.Level.UNDERGROUND, null).possibleClaimCards();
        assertEquals("[{2×BLACK}, {2×VIOLET}, {2×BLUE}, {2×GREEN}, {2×YELLOW}, {2×ORANGE}, {2×RED}, {2×WHITE}, {BLACK, LOCOMOTIVE}, {VIOLET, LOCOMOTIVE}, {BLUE, LOCOMOTIVE}, {GREEN, LOCOMOTIVE}, {YELLOW, LOCOMOTIVE}, {ORANGE, LOCOMOTIVE}, {RED, LOCOMOTIVE}, {WHITE, LOCOMOTIVE}, {2×LOCOMOTIVE}]", cards.toString());
    }

    @Test
    void possibleClaimCardsWorksWithLongerUndergroundRoute(){
        assertEquals("[{3×BLACK}, {3×VIOLET}, {3×BLUE}, {3×GREEN}, {3×YELLOW}, {3×ORANGE}, {3×RED}, {3×WHITE}, {2×BLACK, LOCOMOTIVE}, {2×VIOLET, LOCOMOTIVE}, {2×BLUE, LOCOMOTIVE}, {2×GREEN, LOCOMOTIVE}, {2×YELLOW, LOCOMOTIVE}, {2×ORANGE, LOCOMOTIVE}, {2×RED, LOCOMOTIVE}, {2×WHITE, LOCOMOTIVE}, {BLACK, 2×LOCOMOTIVE}, {VIOLET, 2×LOCOMOTIVE}, {BLUE, 2×LOCOMOTIVE}, {GREEN, 2×LOCOMOTIVE}, {YELLOW, 2×LOCOMOTIVE}, {ORANGE, 2×LOCOMOTIVE}, {RED, 2×LOCOMOTIVE}, {WHITE, 2×LOCOMOTIVE}, {3×LOCOMOTIVE}]", testRoute.possibleClaimCards().toString());
    }

    @Test
    void possibleClaimCardsWorksWithEveryLength(){
        IntStream.range(1, 7).forEach(
            n -> {
                int expectedSize = (n * 8) + 1;
                List<SortedBag<Card>> cards = new Route("AT1_STG_1", SIO, SCZ, n, Route.Level.UNDERGROUND, null).possibleClaimCards();
                assertEquals(expectedSize, cards.size());
            }
        );
    }

    @Test
    void additionalClaimCardsCountFailsWrongDrawnCardsSize(){
        SortedBag<Card> emptyCards = of();
        assertThrows(IllegalArgumentException.class, ()->{
            testRoute.additionalClaimCardsCount(emptyCards, emptyCards);
        });
    }

    @Test
    void additionalClaimCardsCountFailsWrongLevel(){
        SortedBag<Card> emptyCards = of();
        Route r = new Route("AT1_STG_1", SIO, SCZ, 3, Route.Level.OVERGROUND, null);
        assertThrows(IllegalArgumentException.class, ()->{
            r.additionalClaimCardsCount(emptyCards, emptyCards);
        });
    }

   // @Test // TODO additional
    void additionalClaimCardsCountFailsWrongNumberOfClaimCards(){
        SortedBag<Card> claimCards = of(3, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = of(2, Card.LOCOMOTIVE, 1, Card.BLUE);
        Route r = new Route("AT1_STG_1", SIO, SCZ, 4, Route.Level.UNDERGROUND, null);
        assertThrows(IllegalArgumentException.class, ()->{
            r.additionalClaimCardsCount(claimCards, drawnCards);
        });
    }

    @Test
    void additionalClaimCardsCountWorksWithAllLocomotivesColoredRoute(){
        SortedBag<Card> claimCards = of(2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = of(2, Card.LOCOMOTIVE, 1, Card.BLUE);
        Route r = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);
        assertEquals(2, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithAllLocomotivesNeutralRoute(){
        SortedBag<Card> claimCards = of(4, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = of(2, Card.LOCOMOTIVE, 1, Card.BLUE);
        Route r = new Route("AT1_STG_1", SIO, SCZ, 4, Route.Level.UNDERGROUND, null);
        assertEquals(2, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithColoredTunnelAllColoredClaimCards(){
        SortedBag<Card> claimCards = of(2, Card.GREEN);
        SortedBag<Card> drawnCards = of(2, Card.BLACK, 1, Card.BLUE);
        Route r = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);
        assertEquals(0, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithColoredTunnelColoredAndLocomotiveClaimCards(){
        SortedBag<Card> claimCards = of(2, Card.GREEN);
        SortedBag<Card> drawnCards = of(2, Card.GREEN, 1, Card.LOCOMOTIVE);
        Route r = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);
        assertEquals(3, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithNeutralTunnelAllColoredClaimCards(){
        SortedBag<Card> claimCards = of(4, Card.RED);
        SortedBag<Card> drawnCards = of(2, Card.RED, 1, Card.BLUE);
        Route r = new Route("AT1_STG_1", SIO, SCZ, 4, Route.Level.UNDERGROUND, null);
        assertEquals(2, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithNeutralTunnelColoredAndLocomotiveClaimCards(){
        SortedBag<Card> claimCards = of(2, Card.RED, 2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = of(2, Card.RED, 1, Card.LOCOMOTIVE);
        Route r = new Route("AT1_STG_1", SIO, SCZ, 4, Route.Level.UNDERGROUND, null);
        assertEquals(3, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void additionalClaimCardsCountWorksWithNeutralTunnelLocomotiveAndColoredClaimCards(){
        SortedBag<Card> claimCards = of( 2, Card.LOCOMOTIVE, 2, Card.RED);
        SortedBag<Card> drawnCards = of(1, Card.RED, 2, Card.LOCOMOTIVE);
        Route r = new Route("AT1_STG_1", SIO, SCZ, 4, Route.Level.UNDERGROUND, null);
        assertEquals(3, r.additionalClaimCardsCount(claimCards, drawnCards));
    }

    // ----------------------------------- MANDATORY TESTS --------------------------------------------

    private static final List<Color> COLORS =
            List.of(
                    Color.BLACK,
                    Color.VIOLET,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.ORANGE,
                    Color.RED,
                    Color.WHITE);
    private static final List<Card> CAR_CARDS =
            List.of(
                    Card.BLACK,
                    Card.VIOLET,
                    Card.BLUE,
                    Card.GREEN,
                    Card.YELLOW,
                    Card.ORANGE,
                    Card.RED,
                    Card.WHITE);

    @Test
    void routeConstructorFailsWhenBothStationsAreEqual() {
        var s = new Station(0, "Lausanne");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s, s, 1, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLengthIsInvalid() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 0, Route.Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 7, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenIdIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route(null, s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenOneStationIsNull() {
        var s = new Station(0, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", null, s, 1, Route.Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s, null, 1, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLevelIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s1, s2, 1, null, Color.BLACK);
        });
    }

    @Test
    void routeIdReturnsId() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var routes = new Route[100];
        for (int i = 0; i < routes.length; i++)
            routes[i] = new Route("id" + i, s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);
        for (int i = 0; i < routes.length; i++)
            assertEquals("id" + i, routes[i].id());
    }

    @Test
    void routeStation1And2ReturnStation1And2() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Route.Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.station1());
            assertEquals(s2, r.station2());
        }
    }

    @Test
    void routeLengthReturnsLength() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[6];
        for (var l = 1; l <= 6; l++)
            routes[l - 1] = new Route(id, s1, s2, l, Route.Level.OVERGROUND, Color.BLACK);
        for (var l = 1; l <= 6; l++)
            assertEquals(l, routes[l - 1].length());

    }

    @Test
    void routeLevelReturnsLevel() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var ro = new Route(id, s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);
        var ru = new Route(id, s1, s2, 1, Route.Level.UNDERGROUND, Color.BLACK);
        assertEquals(Route.Level.OVERGROUND, ro.level());
        assertEquals(Route.Level.UNDERGROUND, ru.level());
    }

    @Test
    void routeColorReturnsColor() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[8];
        for (var c : COLORS)
            routes[c.ordinal()] = new Route(id, s1, s2, 1, Route.Level.OVERGROUND, c);
        for (var c : COLORS)
            assertEquals(c, routes[c.ordinal()].color());
        var r = new Route(id, s1, s2, 1, Route.Level.OVERGROUND, null);
        assertNull(r.color());
    }

    @Test
    void routeStationsReturnsStations() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Route.Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            assertEquals(List.of(s1, s2), routes[i].stations());
        }
    }

    @Test
    void routeStationOppositeFailsWithInvalidStation() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var s3 = new Station(1, "EPFL");
        var r = new Route("id", s1, s2, 1, Route.Level.OVERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            r.stationOpposite(s3);
        });
    }

    @Test
    void routeStationOppositeReturnsOppositeStation() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Route.Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.stationOpposite(s2));
            assertEquals(s2, r.stationOpposite(s1));
        }
    }

    @Test
    void routePossibleClaimCardsWorksForOvergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, color);
                assertEquals(List.of(SortedBag.of(l, card)), r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnOvergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, null);
            var expected = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);

                var expected = new ArrayList<SortedBag<Card>>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;
                    expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                assertEquals(expected, r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, null);

            var expected = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    expected.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
            }
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithColoredCardsOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, matchingCard);
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithLocomotivesOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, Card.LOCOMOTIVE);
                var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(locomotives, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithMixedCards() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 2; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                for (var claimLoc = 1; claimLoc < l; claimLoc++) {
                    var claimCards = SortedBag.of(
                            l - claimLoc, matchingCard,
                            claimLoc, Card.LOCOMOTIVE);
                    var r = new Route(id, s1, s2, l, Route.Level.UNDERGROUND, color);
                    for (var m = 0; m <= 3; m++) {
                        for (var locomotives = 0; locomotives <= m; locomotives++) {
                            var drawnB = new SortedBag.Builder<Card>();
                            drawnB.add(locomotives, Card.LOCOMOTIVE);
                            drawnB.add(m - locomotives, matchingCard);
                            drawnB.add(3 - m, nonMatchingCard);
                            var drawn = drawnB.build();
                            assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                        }
                    }
                }
            }
        }
    }

    @Test
    void routeClaimPointsReturnsClaimPoints() {
        var expectedClaimPoints =
                List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Route.Level.OVERGROUND, Color.BLACK);
            assertEquals(expectedClaimPoints.get(l), r.claimPoints());
        }
    }

}
