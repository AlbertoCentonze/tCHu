package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TrailTest {
    private static List<Route> getRoutesListUsingFromIds(List<String> ids){
        List<Route> routes = new ArrayList<>();
        return routes.stream().filter(route -> {
            for (String id : ids){
                if (route.id().equals(id)){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Test
    void longestWorksWithEmptyList() {
        List<String> ids = new ArrayList<>(Arrays.asList("NEU_YVE_1", "BER_NEU_1", "BER_LUC_1", "BER_FRI_1", "BER_SOL_1", "NEU_SOL_1"));
        List<Route> routes = getRoutesListUsingFromIds(ids);
        int expectedLength = 13;
        assertEquals(expectedLength, Trail.longest(routes).length());
    }
}
