package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {
    private final Station SCZ = new Station(24, "Schwyz");
    private final Station SIO = new Station(25, "Sion");
    private final Station SOL = new Station(26, "Soleure");
    private final List<SortedBag<Card>> cards = new Route("AT1_STG_1", SIO, SCZ, 3, Route.Level.UNDERGROUND, null).possibleClaimCards();

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
        assertEquals("[{3×BLACK}, {3×VIOLET}, {3×BLUE}, {3×GREEN}, {3×YELLOW}, {3×ORANGE}, {3×RED}, {3×WHITE}, {2×BLACK, LOCOMOTIVE}, {BLACK, 2×LOCOMOTIVE}, {2×VIOLET, LOCOMOTIVE}, {VIOLET, 2×LOCOMOTIVE}, {2×BLUE, LOCOMOTIVE}, {BLUE, 2×LOCOMOTIVE}, {2×GREEN, LOCOMOTIVE}, {GREEN, 2×LOCOMOTIVE}, {2×YELLOW, LOCOMOTIVE}, {YELLOW, 2×LOCOMOTIVE}, {2×ORANGE, LOCOMOTIVE}, {ORANGE, 2×LOCOMOTIVE}, {2×RED, LOCOMOTIVE}, {RED, 2×LOCOMOTIVE}, {2×WHITE, LOCOMOTIVE}, {WHITE, 2×LOCOMOTIVE}, {3×LOCOMOTIVE}]", cards.toString());
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
}
