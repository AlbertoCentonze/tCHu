package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrailTest2 {
    @Test
    void longestOnUsualRoutes() {
        List<Route> routes = List.of(ChMap.routes().get(66), ChMap.routes().get(65), ChMap.routes().get(19), ChMap.routes().get(18), ChMap.routes().get(13), ChMap.routes().get(16));
        Trail tr = Trail.longest(routes);
        assertEquals(13,Trail.longest(routes).length());
        System.out.println(tr);
    }

    @Test
    void longestOnUsualRoutes2() {
        List<Route> routes = List.of(ChMap.routes().get(66), ChMap.routes().get(65), ChMap.routes().get(19));
        Trail tr = Trail.longest(routes);
        assertEquals(8,Trail.longest(routes).length());
        System.out.println(tr);
    }

    @Test
    void longestOnUnsualRoutes() {
        Trail tr = Trail.longest(ChMap.routes().subList(0,54));
        System.out.println(tr.toString(true));
        assertEquals(79,tr.length());
        System.out.println(tr);
    }

    @Test
    void longestOnNullRoutes() {
        List<Route> routes = new ArrayList<>(); // TODO on null routes ??
        Trail tr = Trail.longest(routes);
        assertEquals(0,Trail.longest(routes).length());
        assertNull(Trail.longest(routes).station1());
        assertNull(Trail.longest(routes).station2());
    }

    @Test
    void longestOnZeroRoutes() {
        List<Route> routes = new ArrayList<>();
        Trail tr = Trail.longest(routes);
        assertEquals(0,Trail.longest(routes).length());
        assertNull(Trail.longest(routes).station1());
        assertNull(Trail.longest(routes).station2());
    }

    @Test
    void returnNullOnZeroLength(){
        Trail tr = Trail.longest(new ArrayList<>()); // TODO null ??
        assertNull(tr.station1());
        assertNull(tr.station2());
    }
    @Test
    void textIsCorrectForTrail() {
        ArrayList<Route> r = new ArrayList<>();
        r.add(ChMap.routes().get(16));//berne luc
        r.add(ChMap.routes().get(18));//berne neu
        r.add(ChMap.routes().get(65));// neu sol
        r.add(ChMap.routes().get(19));// berne sol
        r.add(ChMap.routes().get(13));//berne fri
        //r.add(ChMap.routes().get(66));// neu yve

        var s3 = ChMap.stations().get(16);//luc
        var s4 = ChMap.stations().get(9);//fri
        var t = Trail.longest(r);
        assertEquals(13, t.length());
        System.out.println(t.toString(true));
    }
}
