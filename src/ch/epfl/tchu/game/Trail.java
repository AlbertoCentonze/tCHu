package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Trail {

    // length of the trail (sum of the lengths of the routes)
    final private int length;
    // list of routes that compose the trail
    final private List<Route> routes;

    // initial station of the trail
    final private Station station1;
    // final station of the trail
    final private Station station2;

    /**
     * Internal Constructor of Trail
     * @param routes
     * @param station1
     * @param station2
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
     * Constructor for Trail with empty list of routes
     * @param routes
     */
    private Trail(List<Route> routes) {
        this.routes = routes;
        this.station1 = null;
        this.station2 = null;
        this.length = 0;
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
            return new Trail(Collections.emptyList());
        }
        List<Trail> allTrails = new ArrayList<>();

        Trail longest = new Trail(Collections.emptyList());

        // create a copy of the list routes, adding the inverted routes
        for (Route r: routes) {
            allTrails.add(new Trail(Collections.singletonList(r), r.station1(), r.station2()));
            // compute inverted routes
            Route inverseRoute = computeInverseRoute(r);
            allTrails.add(new Trail(Collections.singletonList(inverseRoute), inverseRoute.station1(), inverseRoute.station2()));
        }

        while (!allTrails.isEmpty()) {
            List<Trail> trailsToAdd = new ArrayList<>();
            for (Trail trail: allTrails) {

                // remove all the routes in trail from a copy of the list of built routes
                List<Route> routesNotInTrail = new ArrayList<>(routes);
                routesNotInTrail.removeAll(trail.routes);

                // extend trail with routes which have a station equivalent to the last station of the trail
                for(Route r : routesNotInTrail) {
                    if(r.stations().contains(trail.station2) && !r.stationOpposite(trail.station2).equals(trail.station1)) {

                        List<Route> extendedRoute = new ArrayList<>(trail.routes);
                        extendedRoute.add(r);

                        // create a new trail whose station2 is the station of the added route r that is opposite to the previous station2
                        Trail extendedTrail = new Trail(extendedRoute, trail.station1, r.stationOpposite(trail.station2)); // TODO new constructor
                        if (longest.length() < extendedTrail.length()){
                            longest = extendedTrail;
                        }
                        trailsToAdd.add(extendedTrail);
                    }
                }
            }
            allTrails = trailsToAdd;
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

    /*public String toString(boolean debug) {
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
    }*/

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
