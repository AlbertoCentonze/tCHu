package ch.epfl.tchu.game;

public interface StationConnectivity {

    /**
     * public abstract method to show whether two stations are connected
     * @param (Station) s1
     * @param (Station) s2
     * @return (boolean) false by default
     */
    default boolean connected(Station s1, Station s2) {return false;}
}
