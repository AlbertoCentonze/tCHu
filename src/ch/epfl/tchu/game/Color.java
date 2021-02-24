package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alberto Centonze (327267)
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    public static final List<Color> ALL = List.of(Color.values());

    public static final int COUNT = List.of(Color.values()).size();
}
