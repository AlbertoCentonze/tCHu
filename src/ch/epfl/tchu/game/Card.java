package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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
    public static final List<Card> CARS = ALL.subList(0,COUNT-1);

    // color of card
    // null if locomotive
    private final Color color;

    /**
     * type of Card corresponding to the color
     * @param color
     * @return Card
     */
    public static Card of(Color color) { return CARS.get(color.ordinal()); }

    /**
     * color of the card
     * @return Color
     */
    public Color color() {
        return this.color;
    }

    /**
     * private constructor
     * initializes attribute color
     */
    Card() {
        // gets the index of the corresponding color
        int colorIndex = this.ordinal();
        // assign the right color or null if it's locomotive
        this.color = colorIndex == 8 ? null : Color.values()[colorIndex];
    }
}
