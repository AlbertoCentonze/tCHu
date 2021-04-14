package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alberto Centonze (327267)
 * @author Emma Poggiolini (330757)
 */
public final class Trail {
    // length of the trail (sum of the lengths of the routes)
    final private int length;
    // list of routes that compose the trail
    final private List<Route> routes;

    /**
     * Internal Constructor of Trail
     * @param routes
     */
    private Trail(List<Route> routes) {
        if (routes.size() == 0){
            this.routes = null;
            length = 0;
        }else{
            this.routes = List.copyOf(routes);
            int totalLength = 0;
            for (Route r : routes){
                totalLength += r.length();
            }
            length = totalLength;
        }
    }

    /**
     * Invert the route r
     * @param r
     * @return (Route) inverted route
     */
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

    /**
     * Calculates the longest trail among those built by the player
     * @param routes built by player
     * @return (Trail) longest trail
     */
    public static Trail longest(List<Route> routes){
        // player hasn't built any lists
        if (routes.size() == 0){
            return new Trail(Collections.emptyList());
        }
        List<Trail> cs = new ArrayList<>();
        List<Route> routesWithInverses = new ArrayList<>();

        // create a copy of the list routes, adding the inverted routes
        Trail longest = new Trail(Collections.emptyList());
        for (Route r: routes) {
            routesWithInverses.add(r);
            Route inverseRoute = computeInverseRoute(r);
            routesWithInverses.add(inverseRoute);
        }
        for (Route r : routesWithInverses){
            cs.add(new Trail(Collections.singletonList(r)));
        };
        while (!cs.isEmpty()){
            List<Trail> cs1 = new ArrayList<>();
            //System.out.println(cs.size());
            for (Trail c: cs){
                //System.out.println(c.toString(true));
                for(Route r : routesWithInverses) {
                    if(!c.routes.contains(r) && r.station1().equals(c.station2())
                            && !r.station2().equals(c.routes.get(c.routes.size()-1).station1())) {
                        boolean directRoute = routesWithInverses.indexOf(r) % 2 == 0;
                        boolean isOppositeRouteInTrail = c.routes.contains(routesWithInverses.get(routesWithInverses.indexOf(r) + (directRoute ?  1 : -1)));
                        if(isOppositeRouteInTrail) {
                            continue;
                        }
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
        if (longest.length() == 0){
            int longestIndex = 0;
            int longestLength = 0;
            for (int i = 0; i < routes.size(); ++i){
                Route r = routes.get(i);
                if (r.length() > longestLength){
                    longestIndex = i;
                    longestLength = r.length();
                }
            }
            return new Trail(Collections.singletonList(routes.get(longestIndex)));
        }
        return longest;
    }

    @Override
    public String toString() {
        if (length <= 0){
            return "Empty trail";
        }
        return String.format("%s - %s (%d)", station1().name(), station2().name(), length);
    }

    /**
     * length of the trail (the sum of the length of each route)
     * @return (int) length
     */
    public int length() {
        return length;
    }

    /**
     * getter for departure station
     * @return (Station) station1
     */
    public Station station1() {
        //if the Trail has length 0 empty returns null
        if (length == 0){
            return null;
        }
        return routes.get(0).station1();
    }

    /**
     * getter for arrival station
     * @return (Station) station2
     */
    public Station station2() {
        //if the Trail has length 0 empty returns null
        if (length == 0){
            return null;
        }
        return routes.get(routes.size() - 1).station2();
    }

}
