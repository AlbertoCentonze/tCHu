package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Trail {
    final private int length;
    final private List<Route> routes;

    private Trail(List<Route> routes) {
        //TODO directly in the constructor?
        if (routes.size() == 0){
            this.routes = null;
            this.length = 0;
        }else{
            this.routes = routes;
            int totalLength = 0;
            for (Route r : routes){
                totalLength += r.length();
            }
            this.length = totalLength;
        }
    }

    public static Trail longest(List<Route> routes){
        if (routes.size() == 0){
            return new Trail(null); //TODO fix this
        }
        List<Trail> cs = new ArrayList<>();
        for (Route r: routes){
            cs.add(new Trail(Collections.singletonList(r)));
            Route inverseRoute = new Route(r.id(), r.station2(), r.station1(), r.length(), r.level(), r.color());
            cs.add(new Trail(Collections.singletonList(inverseRoute));
        }
        while (!cs.isEmpty()){
            List<Trail> cs1 = new ArrayList<>();
            for (Trail c: cs){
                for(Route r : routes) {
                    if(!c.routes.contains(r) && r.stations().contains(c.station2())) {
                        List<Route> copy =  ArrayList<>(c.routes);
                        cs1.add(new Trail(new ArrayList<Route>(Arrays.asList(c.routes,r))));
                    }
                }

            }
            cs = cs1;
        }
        return new Trail(0, null); //TODO fix this
    }

    @Override
    public String toString() {
        return String.format("%s - %s ( + %f + )", station1().name(), station2().name(), length);
    }

    public String toString(boolean debug) {
        if (debug){
            return ""; //TODO complete output
        }
        else{
            return toString();
        }
    }

    public int length() {
        return length;
    }

    public Station station1() {
        if (this.length == 0){
            return null;
        }
        return this.routes.get(0).station1();
    }

    public Station station2() {
        if (length == 0){
            return null;
        }
        return this.routes.get(routes.size() - 1).station2();
    }

}
