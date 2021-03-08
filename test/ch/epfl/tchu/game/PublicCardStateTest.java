package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicCardStateTest {
    @Test
    void constructorFailsWithWrongArguments(){
        assertThrows(IllegalArgumentException.class, ()->{
            Card card = Card.BLUE;
            List<Card> fourCardsList = Collections.nCopies(4, card);
            List<Card> fiveCardsList = Collections.nCopies(5, card);
            List<Card> sixCardsList = Collections.nCopies(6, card);
            new PublicCardState(fourCardsList, 0, 0);
            new PublicCardState(sixCardsList, -1, 0);
            new PublicCardState(fiveCardsList, 0, -1);
        });
    }

    @Test
    void faceUpCardFailsOutOfBound(){
        IntStream.range(-10, 0).forEach((n) ->{
            assertThrows(IndexOutOfBoundsException.class, ()-> {
                new PublicCardState(Collections.nCopies(5, Card.RED), 68, 45).faceUpCard(5);
            });
        });
        IntStream.range(6, 10).forEach((n) ->{
            assertThrows(IndexOutOfBoundsException.class, ()-> {
                new PublicCardState(Collections.nCopies(5, Card.RED), 68, 45).faceUpCard(5);
            });
        });
    }

}
