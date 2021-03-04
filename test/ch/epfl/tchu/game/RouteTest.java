package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
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
}
