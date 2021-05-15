package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Alberto Centonze (327267)
 * @author Emma Poggiolini (330757)
 * Trail
 * concatenation of routes
 * used to assign bonus points to the player with the longest trail
 */
public final class Trail {
    // length of the trail (sum of the lengths of the routes)
    final private int length;
    // list of routes that compose the trail
    final private List<Route> routes;
    final private static Trail emptyTrail = new Trail(Collections.emptyList());

    /**
     * Internal Constructor of Trail
     * @param routes of the trail
     */
    private Trail(List<Route> routes) { //TODO Il aurait été plus judicieux de calculer la longueur du nouveau trail à partir de la longueur du trail étendu.
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

    /**
     * Invert the route r
     * @param r the route to invert
     * @return (Route) inverted route
     */
    private static Route computeInverseRoute(Route r) {
        return new Route(r.id(), r.station2(), r.station1(), r.length(), r.level(), r.color());
    }

    /**
     * Calculates the longest trail among those built by the player
     * @param routes built by player
     * @return (Trail) longest trail
     */
    public static Trail longest(List<Route> routes){
        // player hasn't built any lists
        if (routes.size() == 0)
            return emptyTrail;

        Trail longest = emptyTrail;
        List<Route> routesWithInverses = new ArrayList<>();
        // create a copy of the list routes, adding the inverted routes
        for (Route r: routes) {
            routesWithInverses.add(r);
            Route inverseRoute = computeInverseRoute(r);
            routesWithInverses.add(inverseRoute);
        }

        List<Trail> trails = routesWithInverses.stream()
                .map(r -> new Trail(List.of(r)))
                .collect(Collectors.toList());

        while (!trails.isEmpty()){
            List<Trail> newTrails = new ArrayList<>();
            for (Trail t: trails){
                for(Route r : routesWithInverses) {
                    if(!t.routes.contains(r) && r.station1().equals(t.station2())
                            && !r.station2().equals(t.routes.get(t.routes.size()-1).station1())) {
                        boolean directRoute = routesWithInverses.indexOf(r) % 2 == 0;
                        boolean isOppositeRouteInTrail = t.routes.contains(
                                routesWithInverses.get(routesWithInverses.indexOf(r) +
                                        (directRoute ?  1 : -1)));
                        if(isOppositeRouteInTrail) {
                            continue;
                        }

                        List<Route> extendedRoute = new ArrayList<>(t.routes);
                        extendedRoute.add(r);
                        Trail extendedTrail = new Trail(extendedRoute);

                        // check if the extended trail is longer
                        if (longest.length() < extendedTrail.length()){
                            longest = extendedTrail;
                        }
                        newTrails.add(extendedTrail);
                    }
                }
            }
            trails = newTrails;
        }

        // if all the routes are disconnected
        if (longest.length() == 0){
            int longestLengthIndex = IntStream.range(0, routes.size())
                    .reduce(0, (longestIndex, currentIndex) ->
                            routes.get(longestIndex).length() >= routes.get(currentIndex).length() ?
                                    longestIndex : currentIndex);
            return new Trail(List.of(routes.get(longestLengthIndex)));
        } //TODO Le test sur la longueur est inutile.
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
