package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicPlayerStateTest {

    private final List<Route> emptyRoutes = new ArrayList<>();
    private final PublicPlayerState playerEmptyRoutes = new PublicPlayerState(3, 3, emptyRoutes);


    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station NEU = new Station(19, "Neuchâtel");

    private static final Station BRI = new Station(4, "Brigue");
    private static final Station IT5 = new Station(46, "Italie");

    private final Route r = new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null);
    private final Route tunnel = new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN);

    private final List<Route> routes = List.of(r, tunnel);
    PublicPlayerState player = new PublicPlayerState(5, 7, routes);

    @Test
    void ticketCountWorks() {
        assertEquals(5, player.ticketCount());
    }

    @Test
    void constructorFailsIfNegativeTickets() {
        assertThrows(IllegalArgumentException.class, () -> { new PublicPlayerState(-1, 7, routes); });
    }

    @Test
    void cardCountWorks() {
        assertEquals(7, player.cardCount());
    }

    @Test
    void constructorFailsIfNegativeCards() {
        assertThrows(IllegalArgumentException.class, () -> { new PublicPlayerState(0, -4, routes); });
    }

    @Test
    void routesWorks() {
        assertEquals(routes, player.routes());
    }

    @Test
    void routesWorksWithEmptyRoutes() {
        assertEquals(Collections.emptyList(), playerEmptyRoutes.routes());
        // check routes is immutable
        // player.routes().add(r); YES it's immutable
    }

    @Test
    void carCountWorks() {
        assertEquals(33, player.carCount());
    }

    @Test
    void carCountWorksWithEmptyRoutes() {
        assertEquals(40, playerEmptyRoutes.carCount());
    }

    @Test
    void claimPointsWorks() {
        assertEquals(11, player.claimPoints());
    }

    @Test
    void claimPointsWorksWithEmptyRoutes() {
        assertEquals(0, playerEmptyRoutes.claimPoints());
    }
}
