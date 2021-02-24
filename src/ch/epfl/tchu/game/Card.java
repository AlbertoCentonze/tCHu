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
    public final static List<Card> CARS = List.of(ALL.remove(COUNT)); //remove

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
        return color; // is defensive copy necessary ??
    }

    /**
     * private constructor
     * initializes attribute color
     */
    //TODO fix this, private constructor is never called
    private Card() {
        Color color1;    // switch case too repetitive
        switch (this) {
            case BLACK:
                color1 = Color.BLACK;
            case VIOLET:
                color1 = Color.VIOLET;
            case BLUE:
                color1 = Color.BLUE;
            case GREEN:
                color1 = Color.GREEN;
            case YELLOW:
                color1 = Color.YELLOW;
            case ORANGE:
                color1 = Color.ORANGE;
            case RED:
                color1 = Color.RED;
            case WHITE:
                color1 = Color.WHITE;
            default:
                color1 = null;
        }
        this.color = color1; // TODO IntelliJ suggested to create temporary variable color1 (otherwise gave error)
    }
}
