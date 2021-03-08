package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Trail {

    // length of the trail (sum of the lengths of the routes)
    final private int length;
    // list of routes that compose the trail
    final private List<Route> routes;

    final private Station station1;
    final private Station station2;

    /**
     * Internal Constructor of Trail
     * @param routes
     */
    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
        int totalLength = 0;
        for (Route r : routes){
            totalLength += r.length();
        }
        this.length = totalLength;
    }

    /**
     * Invert the route r
     * @param r
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
        if (routes.size() == 0) {
            return new Trail(Collections.emptyList(), null, null);
        }
        List<Trail> cs = new ArrayList<>();

        // create a copy of the list routes, adding the inverted routes
        Trail longest = new Trail(Collections.emptyList(), null, null);
        for (Route r: routes) {
            cs.add(new Trail(Collections.singletonList(r), r.station1(), r.station2()));

            // compute inverted routes
            Route inverseRoute = computeInverseRoute(r);
            cs.add(new Trail(Collections.singletonList(inverseRoute), inverseRoute.station1(), inverseRoute.station2()));
        }

        while (!cs.isEmpty()) {
            List<Trail> cs1 = new ArrayList<>();
            for (Trail c: cs) {
                System.out.println(c.toString(true));
                List<Route> routesNotInTrail = new ArrayList<>(routes);
                routesNotInTrail.removeAll(c.routes);

                // extend trail with routes whose first station is equivalent to the last station of the trail
                for(Route r : routesNotInTrail) {
                    if(r.stations().contains(c.station2) && !r.stationOpposite(c.station2).equals(c.station1)) {

                        List<Route> extendedRoute = new ArrayList<>(c.routes);
                        extendedRoute.add(r);

                        Trail extendedTrail = new Trail(extendedRoute, c.station1, r.stationOpposite(c.station2)); // TODO new constructor
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
        return String.format("%s - %s (%d)", station1().name(), station2().name(), this.length);
    }

    public String toString(boolean debug) {
        if (debug){
            String output = station1.name();
            Station temp = station1;
            for (Route r : this.routes){
                output += " - " + r.stationOpposite(temp).name();
                temp = r.stationOpposite(temp);
            }
            output += " (" + this.length + ")";
            return output;
        }
        else{
            return toString();
        }
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
        return station1;
    }

    /**
     * getter for arrival station
     * @return (Station) station2
     */
    public Station station2() {
        return station2;
    }

}
