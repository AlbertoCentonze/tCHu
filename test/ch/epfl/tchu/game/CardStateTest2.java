package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest2 extends DeckTest2{
    @Test
    void initTest(){
        var cards = new ChCards();
        CardState cS = CardState.of(cards.rDeck);
        assertEquals(105, cS.deckSize() );
        assertEquals(0, cS.discardsSize());
        assertEquals(5, cS.faceUpCards().size());
        cS = cS.withDrawnFaceUpCard(4);
        assertEquals(104, cS.deckSize() );
        // assertEquals(1, cS.discardsSize());
        assertEquals(5, cS.faceUpCards().size());
    }

    @Test
    void initTestOnEmpty(){
        var cards = new ChCards();
        assertThrows(IllegalArgumentException.class, () -> {
            CardState cS = CardState.of(cards.eDeck);
        });
    }
    @Test
    void initTestOnFour(){
        var cards = new ChCards();
        assertThrows(IllegalArgumentException.class, () -> {
            CardState cS = CardState.of(cards.fDeck.withoutTopCard());
        });
    }

    @Test
    void initTestOnFive(){
        var cards = new ChCards();
        CardState cS = CardState.of(cards.fDeck);
        assertEquals(0, cS.deckSize() );
        assertEquals(0, cS.discardsSize());
        assertEquals(5, cS.faceUpCards().size());
        cS = cS.withMoreDiscardedCards(cards.cards);
        assertEquals(0, cS.deckSize() );
        assertEquals(110, cS.discardsSize());
        assertEquals(5, cS.faceUpCards().size());
        cS= cS.withDeckRecreatedFromDiscards(cards.rng);
        assertEquals(110, cS.deckSize() );
        assertEquals(0, cS.discardsSize());
        assertEquals(5, cS.faceUpCards().size());
    }
    @Test
    void ErrorCheck(){
        var cards = new ChCards();
        CardState cS = CardState.of(cards.rDeck);
        CardState cS1 = CardState.of(cards.fDeck);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cS.withDrawnFaceUpCard(5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cS1.withDrawnFaceUpCard(4);
        });
        assertThrows(IllegalArgumentException.class, cS1::topDeckCard);
        assertThrows(IllegalArgumentException.class, cS1::withoutTopDeckCard);
        assertThrows(IllegalArgumentException.class, () -> {
            cS.withDeckRecreatedFromDiscards(cards.rng);
        });
    }
}
