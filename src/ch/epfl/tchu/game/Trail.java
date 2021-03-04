package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Trail {
    final private int length;
    final private List<Route> routes;

    private Trail(List<Route> routes) {
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
        List<Route> cs2 = new ArrayList<>();

        Trail longest = new Trail(Collections.emptyList());
        for (Route r: routes){
            cs.add(new Trail(Collections.singletonList(r)));
            cs2.add(r);

            Route inverseRoute = computeInverseRoute(r);
            cs.add(new Trail(Collections.singletonList(inverseRoute)));
            cs2.add(inverseRoute);
        }
        while (!cs.isEmpty()){
            List<Trail> cs1 = new ArrayList<>();
            System.out.println(cs.size());
            for (Trail c: cs){
                List<Route> reverse = new ArrayList<>();
                System.out.println(c.toString(true));
                for(Route r : cs2) {
                    //System.out.println(!(c.routes.contains(r)));
                    if(!(c.routes.contains(r)) && r.station1().equals(c.station2())  // !isInverseInList(c.routes, r)
                            && !r.station2().equals(c.routes.get(c.routes.size()-1).station1())) { // !isInverseInList(c.routes, r)
                        if(cs2.indexOf(r)%2 == 0 && c.routes.contains(cs2.get(cs2.indexOf(r)+1))) {
                            break;
                        } else if(cs2.indexOf(r)%2 == 1 && c.routes.contains(cs2.get(cs2.indexOf(r)-1))) {
                            break;
                        }
                        // !c.routes.contains(computeInverseRoute(r))
                        // System.out.println(!c.routes.contains(computeInverseRoute(r)));
                        // System.out.println(computeInverseRoute(r).station1());
                        // System.out.println(computeInverseRoute(r).station2());

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
    public String toString() {
        if (length <= 0){
            return "Empty trail";
        }
        return String.format("%s - %s ( ", station1().name(), station2().name()) + this.length + " )"; //TODO WTF
    }

    public String toString(boolean debug) {
        if (debug){
            String output = "";
            for (Route r : this.routes){
                output += r.station1().name();
                output += " - ";
            }
            output += this.station2();
            output += " ( " + this.length + " )";
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
