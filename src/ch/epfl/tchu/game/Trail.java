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

    private static Route computeInverseRoute(Route r) {
        return new Route(r.id(), r.station2(), r.station1(), r.length(), r.level(), r.color());
    }

    private static boolean isInverseInList(List<Route> routes, Route routeToCheck){
        boolean result = false;
        for (Route r: routes){
            result = result || r.equals(computeInverseRoute(routeToCheck));
        }
        return result;
    }

    public static Trail longest(List<Route> routes){
        if (routes.size() == 0){
            return new Trail(Collections.emptyList());
        }
        List<Trail> cs = new ArrayList<>();
        Trail longest = new Trail(Collections.emptyList());
        for (Route r: routes){
            cs.add(new Trail(Collections.singletonList(r)));
            Route inverseRoute = computeInverseRoute(r);
            cs.add(new Trail(Collections.singletonList(inverseRoute)));
        }
        while (!cs.isEmpty()){
            List<Trail> cs1 = new ArrayList<>();
            System.out.println(cs.size());
            for (Trail c: cs){
                System.out.println(c.toString(true));
                for(Route r : routes) {
                    if(!c.routes.contains(r) && r.stations().contains(c.station2()) && !isInverseInList(c.routes, r)) {
                        List<Route> extendedRoute = new ArrayList<>(c.routes);
                        extendedRoute.add(r);
                        Trail extendedTrail = new Trail(extendedRoute);
                        if (longest.length() < extendedTrail.length()){
                            longest = extendedTrail;
                        }
                        cs1.add(extendedTrail);
                    }
                }
            }
            cs = cs1;
        }
        return longest;
    }

    @Override
    public String toString() { //TODO fix this
        return String.format("%s - %s ( + %o + )", station1().name(), station2().name(), length);
    }

    public String toString(boolean debug) {
        if (debug){
            String output = "";
            for (Route r : this.routes){
                output += r.station1().name();
                output += " - ";
            }
            output += this.station2();
            output += " (" + this.length + " )";
            return output;
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
