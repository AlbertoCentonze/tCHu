package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static ch.epfl.tchu.game.CardTest.pickRandomCard;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void totalSizeWorksAfterNewStateIsGeneratedFromWithDrawnFaceUpCard(){
        IntStream.range(6, 110).forEach(n -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            CardState initialState = CardState.of(cards);
            int expectedTotalSize = initialState.totalSize() - 1;
            CardState modifiedState = initialState.withDrawnFaceUpCard(n % 5);
            assertEquals(expectedTotalSize, modifiedState.totalSize());
        });
    }

    @Test
    void totalSizeWorksAfterNewStateIsGeneratedFromWithoutTopDeckCard(){
        IntStream.range(6, 110).forEach(n -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            CardState initialState = CardState.of(cards);
            int expectedTotalSize = initialState.totalSize() - 1;
            CardState modifiedState = initialState.withoutTopDeckCard();
            assertEquals(expectedTotalSize, modifiedState.totalSize());
        });
    }

    @Test
    void totalSizeWorksAfterNewStateIsGeneratedFromWithDeckRecreatedFromDiscardedCards(){
            Deck<Card> cards = Deck.of(SortedBag.of(5, pickRandomCard()), newRandom());
            CardState initialState = CardState.of(cards);
            IntStream.range(5, 30).forEach(m -> {
                int expectedTotalSize = initialState.totalSize() + m;
                SortedBag<Card> additionalCards = SortedBag.of(m, pickRandomCard());
                CardState state2 = initialState.withMoreDiscardedCards(additionalCards);
                CardState modifiedState = state2.withDeckRecreatedFromDiscards(newRandom());
                assertEquals(expectedTotalSize, modifiedState.totalSize());
            });
    }

    @Test
    void totalSizeWorksAfterNewStateIsGeneratedFromWithMoreDiscardedCards(){
        IntStream.range(6, 110).forEach(n -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            CardState initialState = CardState.of(cards);
            IntStream.range(5, 30).forEach(m -> {
                int expectedTotalSize = initialState.totalSize() + m;
                SortedBag<Card> additionalCards = SortedBag.of(m, pickRandomCard());
                CardState modifiedState = initialState.withMoreDiscardedCards(additionalCards);
                assertEquals(expectedTotalSize, modifiedState.totalSize());
            });
        });
    }
}
