package ch.epfl.tchu.game;

/**
 * @author Emma Poggiolini (330757)
 */
public interface StationConnectivity {

    /**
     * public abstract method to show whether two stations are connected
     * @param s1 : first station
     * @param s2 : second station
     * @return (boolean)
     */
    boolean connected(Station s1, Station s2);
}
