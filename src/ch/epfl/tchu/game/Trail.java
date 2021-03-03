package ch.epfl.tchu.game;

import java.util.ArrayList;
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
        for (Route routeInList: routes){
            cs.add(new Trail(Collections.singletonList(routeInList)));
            Route oppositeRoute;
            for (Route possibleOppositeRoute: ChMap.routes())
                if (possibleOppositeRoute.station1().equals(routeInList.station2()) && possibleOppositeRoute.station2().equals(routeInList.station1())){
                    cs.add(possibleOppositeRoute);
                    break;
                }
        }
        while (cs.size() > 0){
            List<Route> cs1 = new ArrayList<>();
            for (Route route: cs){

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
