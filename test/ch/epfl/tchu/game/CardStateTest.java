package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import java.util.stream.IntStream;


import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class CardStateTest {
    private final CardState initialState = CardState.of(Deck.of(Constants.ALL_CARDS, newRandom()));
    private final CardState emptyDeckState = generateEmptyDeckState();

    private static CardState generateEmptyDeckState() {
        Deck<Card> cards = Deck.of(SortedBag.of(5, pickRandomCard()), newRandom());
        CardState emptyDeckState = CardState.of(cards);
        assert emptyDeckState.isDeckEmpty() == true;
        return emptyDeckState;
    }

    private static Card pickRandomCard() {
        return Card.values()[newRandom().nextInt(Card.values().length)];
    }

    @Test
    void ofFailsWithLessThanFiveCards() {
        IntStream.range(0, 5).forEach((n) -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            assertThrows(IllegalArgumentException.class, () -> {
                CardState.of(cards);
            });
        });
    }


    @Test
    void ofWorksCorrectly() {
        IntStream.range(5, 110).forEach((n) -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            CardState testState = CardState.of(cards);
            assertEquals(testState.faceUpCards().size(), 5);
            assertEquals(testState.deckSize(), n - 5);
            assertEquals(testState.discardsSize(), 0);
        });
    }

    @Test
    void withDrawnFaceUpCardFailsWithSlotOutOfBound() {
        IntStream.range(-5, 0).forEach((n) -> {
            assertThrows(IndexOutOfBoundsException.class, () -> {
                initialState.withDrawnFaceUpCard(n);
            });
        });
        IntStream.range(5, 10).forEach((n) -> {
            assertThrows(IndexOutOfBoundsException.class, () -> {
                initialState.withDrawnFaceUpCard(n);
            });
        });
    }

    @Test
    void withDrawnFaceUpCardFailsWithEmptyDeck() {
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeckState.withDrawnFaceUpCard(5);
        });
    }

    @Test
    void withDrawnFaceUpCardWorksCorrectly() {
        IntStream.range(0, 5).forEach(n -> {
            CardState modifiedInitialState = initialState.withDrawnFaceUpCard(n);
            assertEquals(modifiedInitialState.faceUpCard(n), initialState.topDeckCard());
            List<Card> recreatedFaceUpCards = new ArrayList<>(initialState.faceUpCards());
            recreatedFaceUpCards.set(n, initialState.topDeckCard());
            assertEquals(modifiedInitialState.faceUpCards(), recreatedFaceUpCards);
            assertEquals(modifiedInitialState.deckSize(), initialState.deckSize() - 1);
            assertEquals(initialState.discardsSize(), modifiedInitialState.discardsSize());
        });
    }

    @Test
    void topDeckCardFailsWithEmptyDeck(){
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDeckState.topDeckCard();
        });
    }

    @Test
    void topDeckCardWorksCorrectly(){
        //TODO do I really need to test this?
    }

    @Test
    void withoutTopDeckCardFailsWithEmptyDeck(){
        assertThrows(IllegalArgumentException.class, ()->{
            emptyDeckState.withoutTopDeckCard();
        });
    }

    @Test
    void withoutTopDeckCardWorksCorrectly(){
        assertEquals(initialState.withoutTopDeckCard().deckSize(), initialState.deckSize() - 1);
        //TODO more tests?
    }

    @Test
    void withDeckRecreatedFromDiscardsFailsWithNonEmptyDeck(){
        IntStream.range(6, 110).forEach(n -> {
            Deck<Card> cards = Deck.of(SortedBag.of(n, pickRandomCard()), newRandom());
            CardState state = CardState.of(cards);
            assertThrows(IllegalArgumentException.class, () -> {
                state.withDeckRecreatedFromDiscards(newRandom());
           });
        });
    }

    @Test
    void withDeckRecreatedFromDiscardsWorksCorrectly(){
        CardState recreatedDeck = emptyDeckState.withDeckRecreatedFromDiscards(newRandom());
        assertEquals(recreatedDeck.discardsSize(), emptyDeckState.deckSize());
        assertEquals(recreatedDeck.discardsSize(), 0);
        assertEquals(recreatedDeck.deckSize(), emptyDeckState.discardsSize());
        //TODO additional tests using anonymous classes, are they worth it?
    }

    @Test
    void withMoreDiscardedCardsWorksCorrectly(){
        IntStream.range(0, 110).forEach(n -> {
            SortedBag<Card> additionalDiscardedCards = SortedBag.of(n, pickRandomCard());
            CardState modifiedInitialState = initialState.withMoreDiscardedCards(additionalDiscardedCards);
            CardState modifiedEmptyDeckState = emptyDeckState.withMoreDiscardedCards(additionalDiscardedCards);
            assertEquals(modifiedEmptyDeckState.deckSize(), emptyDeckState.deckSize());
            assertEquals(emptyDeckState.discardsSize() + n, modifiedEmptyDeckState.discardsSize());
        });
    }

}
