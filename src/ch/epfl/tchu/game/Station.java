package ch.epfl.tchu.game;

/**
 * @author Alberto Centonze (327267)
 */
public final class Station {
    // individual station id
    private int id;
    // name of the station
    private String name;

    /**
     * Station default constructor
     * @param id individual station id
     * @param name name of the station
     */
    public Station(int id, String name) {
        if (id < 0){
            throw new IllegalArgumentException(); //TODO use preconditions? check for > 50?
        }
        this.id = id;
        this.name = name;
    }

    /**
     * station id getter
     * @return (int) id
     */
    public int id() {
        return id;
    }

    /**
     * station name getter
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
