package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {
    @Test
    void ofFailsWithLessThanFiveCards(){
        Deck<Card> cards = Deck.of(SortedBag.of(4, Card.BLUE), new Random());
        assertThrows(IllegalArgumentException.class, ()->{
            CardState.of(cards);
        });
    }
}
