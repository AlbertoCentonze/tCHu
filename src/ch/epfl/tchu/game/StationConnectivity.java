package ch.epfl.tchu.game;

/**
 * @author Emma Poggiolini (330757)
 */

public interface StationConnectivity {

    /**
     * public abstract method to show whether two stations are connected
     * @param (Station) s1
     * @param (Station) s2
     * @return (boolean)
     */
    abstract boolean connected(Station s1, Station s2);
}
