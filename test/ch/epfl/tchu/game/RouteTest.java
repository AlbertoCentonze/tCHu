package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {
    private static final Station SCZ = new Station(24, "Schwyz");
    private static final Station SIO = new Station(25, "Sion");
    private static final Station SOL = new Station(26, "Soleure");

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
    void possibleClaimCardsRespectsExample(){
        List<SortedBag<Card>> expectedCards = new ArrayList<>();
        Route r = new Route("AT1_STG_1", SIO, SCZ, 2, Route.Level.UNDERGROUND, null);
        assertEquals("[{2×BLACK}, {2×VIOLET}, {2×BLUE}, {2×GREEN}, {2×YELLOW}, {2×ORANGE}, {2×RED}, {2×WHITE}, {BLACK, LOCOMOTIVE}, {VIOLET, LOCOMOTIVE}, {BLUE, LOCOMOTIVE}, {GREEN, LOCOMOTIVE}, {YELLOW, LOCOMOTIVE}, {ORANGE, LOCOMOTIVE}, {RED, LOCOMOTIVE}, {WHITE, LOCOMOTIVE}, {2×LOCOMOTIVE}]", r.possibleClaimCards().toString());
    }
}
