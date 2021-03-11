package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;


import java.util.Random;

import java.util.stream.IntStream;


import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {
    @Test
    void ofFailsWithLessThanFiveCards(){

        IntStream.range(0, 5).forEach((n) -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, Card.BLUE), newRandom());
            assertThrows(IllegalArgumentException.class, ()->{
                CardState.of(cards);
            });
        });
    }

}
