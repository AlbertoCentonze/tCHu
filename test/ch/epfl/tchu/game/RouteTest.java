package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
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

}
