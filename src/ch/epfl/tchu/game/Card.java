package ch.epfl.tchu.game;

import java.util.List;

public enum Card {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE;

    // list of all the values of Card
    public static final List<Card> ALL = List.of(Card.values());

    // number of values in the enumeration
    public static final int COUNT = ALL.size();

    // list of all railcar cards
    public final static List<Card> CARS = List.of(Card.values()); //remove
}
