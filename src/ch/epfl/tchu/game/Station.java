package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Alberto Centonze (327267) 
 */
public final class Station { 
    // individual station id
    private final int id;
    // name of the station
    private final String name;

    /**
     * Station constructor
     * @param id : individual station id
     * @param name : name of the station
     */
    public Station(int id, String name) {
        // check that the id is non-negative
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the station's id
     * @return (int) id
     */
    public int id() {
        return id;
    }

    /**
     * Getter for the station's name
     * @return (String) name of the station
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
