package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Emma Poggiolini (330757)
 */
public enum Card {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE;

    // list of all the values of Card
    public static final List<Card> ALL = List.of(Card.values());

    // number of values in the enumeration
    public static final int COUNT = ALL.size();

    // list of all railcar cards
    public final static List<Card> CARS = List.of(ALL.remove(COUNT));

    // color of card
    // null if locomotive
    private final Color color;

    /**
     * type of Card corresponding to its color
     * @param color
     * @return Card
     */
    public static Card of(Color color) {
        return CARS.get(color.ordinal());
    }

    /**
     * color of the card
     * @param none
     * @return Color
     */
    public Color color() {
        return color; 
    }

    /**
     * private constructor
     * initializes attribute color
     */
    //TODO fix this, private constructor is never called
    private Card() {
        // TODO initialize color to null for locomotive
        this.color = Color.values()[this.ordinal()];
    }
}
