package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Alberto Centonze (327267)
 */
public enum Color {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    // list of all the values of Card
    public static final List<Color> ALL = List.of(Color.values());

    // number of values in the enumeration
    public static final int COUNT = ALL.size();
}
