package ch.epfl.tchu.game;

/**
 * @author Alberto Centonze (327267)
 */
public final class Station {
    private int id;
    private String name;

    public Station(int id, String name) {
        if (id < 0){
            throw new IllegalArgumentException(); //TODO use preconditions? check for > 50?
        }
        this.id = id;
        this.name = name;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
