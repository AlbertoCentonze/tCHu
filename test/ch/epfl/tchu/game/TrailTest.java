package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TrailTest {
    private Trail emptyTrail = Trail.longest(Collections.emptyList());
    private final List<Route> suggestedRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1")));
    private final List<Route> shorterTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));
    private final List<Route> foreignTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("FR3_LCF_1", "DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));

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

    @Test
    void longestWorksWithSuggestedList() {
        Trail trail = Trail.longest(suggestedRoutes);
        System.out.println(trail.toString(true));
        int expectedLength = 13;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void toStringWorks(){
        assertEquals("Lucerne - Fribourg ( 15 )", Trail.longest(suggestedRoutes).toString());
        assertEquals("Lucerne - Berne - Neuchâtel - Soleure - Berne - Fribourg ( 15 )", Trail.longest(suggestedRoutes).toString(true));

    }

    @Test
    void toStringWorksWithEmptyTrail() {
        assertEquals(emptyTrail.toString(), "Empty trail");
    }

    @Test
    void longestWorksWithShorterList() {
        Trail trail = Trail.longest(shorterTestRoutes);
        System.out.println(trail.toString(true));
        int expectedLength = 12;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithForeignStationList() {
        Trail trail = Trail.longest(foreignTestRoutes);
        System.out.println(trail.toString(true));
        int expectedLength = 14;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithEmptyList() {
        assertEquals(0, emptyTrail.length());
        assertNull(emptyTrail.station1());
        assertNull(emptyTrail.station2());
    }
}
