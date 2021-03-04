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
    private final List<String> ids = new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1"));

    private static List<Route> getRoutesListUsingFromIds(List<String> ids){
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

    void longestLengthIsCorrect() {
        List<Route> routes = getRoutesListUsingFromIds(ids);
        System.out.println(routes);
        Trail trail = Trail.longest(routes);
        System.out.println(trail.toString(true));
        int expectedLength = 13;
        assertEquals(expectedLength, trail.length());
    }

    @Test
    void longestWorksWithEmptyList(){
        assertEquals(0, emptyTrail.length());
        assertNull(emptyTrail.station1());
        assertNull(emptyTrail.station2());
    }

    @Test
    void toStringWorks(){
        //TODO fix longest first
    }

    @Test
    void toStringWorksWithEmptyTrail(){
        assertEquals(emptyTrail.toString(), "Empty trail");
    }
}
