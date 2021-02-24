package ch.epfl.tchu.game;

import java.util.List;

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
    private Color color;

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
    private Card() {    // switch case too repetitive
        switch (this) {
            case BLACK:
                this.color = Color.BLACK;
            case VIOLET:
                this.color = Color.VIOLET;
            case BLUE:
                this.color = Color.BLUE;
            case GREEN:
                this.color = Color.GREEN;
            case YELLOW:
                this.color = Color.YELLOW;
            case ORANGE:
                this.color = Color.ORANGE;
            case RED:
                this.color = Color.RED;
            case WHITE:
                this.color = Color.WHITE;
            default:
                this.color = null;
        }
    }
}
