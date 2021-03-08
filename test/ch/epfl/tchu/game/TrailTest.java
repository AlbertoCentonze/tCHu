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
    private final List<Route> suggestedTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1")));
    private final List<Route> shorterTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));
    private final List<Route> foreignTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("FR3_LCF_1", "DEL_LCF_1", "LCF_YVE_1", "DEL_SOL_1", "LCF_NEU_1", "NEU_SOL_1")));
    private final List<Route> longerTestRoutes = getRoutesListFromIds(new ArrayList<>(Arrays.asList("DEL_SOL_1", "NEU_YVE_1", "BER_NEU_1", "LCF_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "DEL_LCF_1", "NEU_SOL_1")));

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

    //These toString tests while still being correct, may fail if the longest method is changed
    @Test
    void toStringWorksWithSuggestedList() {
        assertEquals("Lucerne - Fribourg (13)", Trail.longest(suggestedTestRoutes).toString());
        //assertEquals("Lucerne - Berne - Neuchâtel - Soleure - Berne - Fribourg (13)", Trail.longest(suggestedTestRoutes).toString(true));
    }
    @Test
    void toStringWorksWithShorterList() {
        assertEquals("La Chaux-de-Fonds - Yverdon (12)", Trail.longest(shorterTestRoutes).toString());
       // assertEquals("La Chaux-de-Fonds - Delémont - Soleure - Neuchâtel - La Chaux-de-Fonds - Yverdon (12)", Trail.longest(shorterTestRoutes).toString(true));
    }
    @Test
    void toStringWorksWithForeignList() {
        assertEquals("Yverdon - France (14)", Trail.longest(foreignTestRoutes).toString());
       // assertEquals("Yverdon - La Chaux-de-Fonds - Delémont - Soleure - Neuchâtel - La Chaux-de-Fonds - France (14)", Trail.longest(foreignTestRoutes).toString(true));
    }
    @Test
    void toStringWorksWithEmptyTrail() {
        assertEquals(emptyTrail.toString(), "Empty trail");
    }

    @Test
    void longestWorksWithSuggestedList() {
        Trail trail = Trail.longest(suggestedTestRoutes);
        //System.out.println(trail.toString(true));
        int expectedLength = 13;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithShorterList() {
        Trail trail = Trail.longest(shorterTestRoutes);
        //System.out.println(trail.toString(true));
        int expectedLength = 12;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithForeignStationList() {
        Trail trail = Trail.longest(foreignTestRoutes);
        //System.out.println(trail.toString(true));
        int expectedLength = 14;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithLongerList() {
        Trail trail = Trail.longest(longerTestRoutes);
        //System.out.println(trail.toString(true));
        int expectedLength = 17;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithEmptyList() {
        assertEquals(0, emptyTrail.length());
        assertNull(emptyTrail.station1());
        assertNull(emptyTrail.station2());
    }
}
