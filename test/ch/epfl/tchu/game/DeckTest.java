package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    public static final Random NON_RANDOM = new Random() {
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };

    private SortedBag<Card> cards = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE, Card.YELLOW, Card.YELLOW, Card.VIOLET, Card.RED));
   /* VIOLET
            GREEN
    YELLOW
            YELLOW
    ORANGE
            ORANGE
    RED
            LOCOMOTIVE */
    private SortedBag<Card> empty = SortedBag.of();

    private SortedBag<Card> cardsShort = SortedBag.of(List.of(Card.WHITE, Card.BLUE, Card.WHITE));
    private Deck<Card> deckShort = Deck.of(cardsShort, NON_RANDOM);

    //private SortedBag<Card> cardsTunnel = SortedBag.of(List.of(Card.LOCOMOTIVE, Card.GREEN, Card.GREEN));

    private Deck<Card> deck = Deck.of(cards, NON_RANDOM);
    private Deck<Card> emptyDeck = Deck.of(empty, NON_RANDOM);

    @Test
    void constructorAndSizeWork() {
        assertEquals(8, deck.size());
    }

    @Test
    void isEmptyWorksWhenNotEmpty() {
        assertFalse(deck.isEmpty());
    }

    @Test
    void isEmptyWorksWhenEmpty() {
        assertTrue(emptyDeck.isEmpty());
    }




    @Test
    void topCardWorksShort() {
        System.out.println(cardsShort.toString());
        for(int i = 0; i < deckShort.size(); ++i) {
            System.out.println(deckShort.get(i).name());
        }
        assertEquals(Card.BLUE, deckShort.topCard()); // TODO in what order does SortedBag sort?
    }




    @Test
    void topCardWorksWhenNotEmpty() {
        for(int i = 0; i < deck.size(); ++i) {
            System.out.println(deck.get(i).name());
        }
        assertEquals(Card.VIOLET, deck.topCard()); // TODO VIOLET instead of ORANGE; in what order does SortedBag sort?
    }

    @Test
    void topCardFailsWhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> { emptyDeck.topCard(); });
    }

    @Test
    void withoutTopCardWorksWhenNotEmpty() {
        Deck withoutTopCard = deck.withoutTopCard();
        assertEquals(7, withoutTopCard.size());
        for(int i = 0; i < withoutTopCard.size(); ++i) {
            assertEquals(deck.get(i+1), withoutTopCard.get(i));
        }
    }

    @Test
    void withoutTopCardFailsWhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> { emptyDeck.withoutTopCard(); });
    }

    @Test
    void topCardsFailsWhenIndexOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> { deck.topCards(-1); });
    }

    @Test
    void topCardsFailsWhenIndexOutOfBoundsBis() {
        assertThrows(IllegalArgumentException.class, () -> { deck.topCards(9); });
    }

    @Test
    void topCardsWorksWhenIndexCorrect() {
        assertEquals(cards, deck.topCards(8));
    }

    // private SortedBag<Card> cards2 = SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.ORANGE, Card.LOCOMOTIVE));
    private SortedBag<Card> cards2 = SortedBag.of(List.of(Card.VIOLET, Card.YELLOW, Card.YELLOW, Card.GREEN));
    @Test
    void topCardsWorksWhenIndexCorrect2() {
        assertEquals(cards2, deck.topCards(4));
    }

    @Test
    void withoutTopCardsFailsWhenIndexOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> { deck.topCards(-1); });
    }

    @Test
    void withoutTopCardsFailsWhenIndexOutOfBoundsBis() {
        assertThrows(IllegalArgumentException.class, () -> { deck.topCards(9); });
    }

    @Test
    void withoutTopCardsWorksWhenIndexCorrect() {
        assertTrue(deck.withoutTopCards(8).isEmpty());
    }

    //private SortedBag<Card> cardsLeft = SortedBag.of(List.of(Card.YELLOW, Card.YELLOW, Card.VIOLET, Card.RED));
    private SortedBag<Card> cardsLeft = SortedBag.of(List.of(Card.LOCOMOTIVE, Card.ORANGE, Card.ORANGE, Card.RED));
    private Deck deckLeft = Deck.of(cardsLeft,NON_RANDOM);
    @Test
    void withoutTopCardsWorksWhenIndexCorrect2() {
        Deck deckWithoutTopCards = deck.withoutTopCards(4);
        for(int i = 0; i < deckWithoutTopCards.size(); ++i) {
            assertEquals(deckLeft.get(i), deckWithoutTopCards.get(i));
        }
    }

}
