package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest2 {

    public static final Random NON_RANDOM = new Random() {
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };


    @Test
    void testOnDeckInit(){
        var deckOfC = new ChCards();
        Deck<Card> deck= Deck.of(deckOfC.cards, deckOfC.rng);
        Deck<Card> deck2= Deck.of(deckOfC.cards, NON_RANDOM);
        Deck<Card> deck3= Deck.of(deckOfC.cards, NON_RANDOM);

        assertEquals(110, deck.size());
        assertFalse(deck.isEmpty());
        System.out.println(deck.topCard());
        System.out.println(deck.topCards(5));
        System.out.println(deck.withoutTopCard().topCards(5));
        assertEquals(105, deck.withoutTopCards(5).size());
        System.out.println(deck2.withoutTopCard().topCards(5));
        System.out.println(deck3.withoutTopCard().topCards(5));
    }

    @Test
    void topCardTestEr(){
        var deckOfC = new ChCards();
        assertThrows(IllegalArgumentException.class, deckOfC.eDeck::topCard);
    }

    @Test
    void withoutTopCardTestEr(){
        var deckOfC = new ChCards();
        assertThrows(IllegalArgumentException.class, deckOfC.eDeck::withoutTopCard);
    }

    @Test
    void topCardsTestEr(){
        var deckOfC = new ChCards();
        assertThrows(IllegalArgumentException.class, () -> {
            deckOfC.rDeck.topCards(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deckOfC.rDeck.topCards(deckOfC.rDeck.size()+1);
        });
        System.out.println(deckOfC.rDeck.topCards(0));
    }

    @Test
    void withoutTopCardsTestEr(){
        var deckOfC = new ChCards();
        assertThrows(IllegalArgumentException.class, () -> {
            deckOfC.rDeck.withoutTopCards(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            deckOfC.rDeck.withoutTopCards(deckOfC.rDeck.size()+1);
        });
        System.out.println(deckOfC.rDeck.topCard());
        System.out.println(deckOfC.rDeck.withoutTopCards(0).topCard());
    }

    @Test
    void deckWorksWithEmptyList(){
        final SortedBag<Card> emptyCard = SortedBag.of();
        final Deck<Card> eDeck= Deck.of(emptyCard, new Random());
        assertTrue(eDeck.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> {eDeck.topCard();});
        assertThrows(IllegalArgumentException.class, () -> {eDeck.topCards(2);});
        assertThrows(IllegalArgumentException.class, () -> {eDeck.withoutTopCard();});
        assertThrows(IllegalArgumentException.class, () -> {eDeck.withoutTopCards(2);});
    }

    @Test
    void deckTopCardsWorksWithIllegalExpressions() {
        var d = Deck.of(SortedBag.of(List.of(1,2,3)), NON_RANDOM);
        assertThrows(IllegalArgumentException.class, () -> {d.topCards(4);});
        assertThrows(IllegalArgumentException.class, () -> {d.withoutTopCards(4);});
    }




    protected static final class ChCards {
        final SortedBag<Card> emptycard = SortedBag.of();
        final Random rng = new Random();
        final SortedBag<Card> fiveCards= SortedBag.of(5,Card.BLACK);
        final SortedBag<Card> cards1= SortedBag.of(12,Card.BLACK,12,Card.VIOLET);
        final SortedBag<Card> cards2= SortedBag.of(12,Card.BLUE,12,Card.GREEN);
        final SortedBag<Card> cards3= SortedBag.of(12,Card.YELLOW,12,Card.ORANGE);
        final SortedBag<Card> cards4= SortedBag.of(12,Card.RED,12,Card.WHITE);
        final SortedBag<Card> cards5= SortedBag.of(14,Card.LOCOMOTIVE);
        final SortedBag<Card> cards = cards1.union(cards2).union(cards3).union(cards4).union(cards5);
        final Deck<Card> rDeck= Deck.of(cards, new Random());
        final Deck<Card> eDeck= Deck.of(emptycard, new Random());
        final Deck<Card> fDeck= Deck.of(fiveCards, new Random());


    }
}
