package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    public static final Object reflect(Deck<Card> deck) throws IllegalAccessException{
        Class<?> classReference = deck.getClass();
        List<Field> fields = Arrays.asList(classReference.getFields());
        fields.stream().filter(f -> f.getName() == "deck");
        try{
            Object cards = fields.get(0).get(deck);
            return cards;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

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
    void topCardFailsWhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> { emptyDeck.topCard(); });
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

    // ----------------------------------- MANDATORY TESTS ----------------------------------------

    @Test
    void deckOfShufflesDeck() {
        var cards = listOfSize(100);
        var cardsBag = SortedBag.of(cards);
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var rng = new Random(i);
            var deck = Deck.of(cardsBag, rng);
            var deckList = deckToList(deck);
            // The shuffled deck *could* be equal to the non-shuffled one,
            // but with 100 elements this is extremely unlikely
            // (and doesn't happen with the random seeds we're using).
            assertNotEquals(cards, deckList);

            var deckSortedList = new ArrayList<>(deckList);
            Collections.sort(deckSortedList);
            assertEquals(cards, deckSortedList);
        }
    }

    @Test
    void deckSizeReturnsSize() {
        for (var size = 0; size < 100; size++) {
            var rng = new Random(size);
            var deck = Deck.of(SortedBag.of(listOfSize(size)), rng);
            assertEquals(size, deck.size());
        }
    }

    @Test
    void deckIsEmptyReturnsTrueOnlyWhenDeckIsEmpty() {
        for (var size = 0; size < 10; size++) {
            var rng = new Random(size);
            var deck = Deck.of(SortedBag.of(listOfSize(size)), rng);
            assertEquals(size == 0, deck.isEmpty());
        }
    }

    @Test
    void deckTopCardFailsWithEmptyDeck() {
        var deck = Deck.of(SortedBag.<String>of(), new Random(2021));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCard();
        });
    }

    @Test
    void deckTopCardReturnsTopCard() {
        var card = "card";
        var deck = Deck.of(SortedBag.of(10, card), new Random(2021));
        assertEquals(card, deck.topCard());
    }

    @Test
    void deckWithoutTopCardFailsWithEmptyDeck() {
        var deck = Deck.of(SortedBag.<String>of(), new Random(2021));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCard();
        });
    }

    @Test
    void deckWithoutTopCardRemovesTopCard() {
        var cards = SortedBag.of(listOfSize(50));
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var actualCardsBuilder = new SortedBag.Builder<Integer>();
        while (!deck.isEmpty()) {
            actualCardsBuilder.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        assertEquals(cards, actualCardsBuilder.build());
    }

    @Test
    void deckTopCardsFailsWithTooSmallDeck() {
        var rng = TestRandomizer.newRandom();
        for (int deckSize = 0; deckSize < 20; deckSize++) {
            var deck = Deck.of(SortedBag.of(deckSize, "card"), rng);
            var tooBigSize = deckSize + 1;
            assertThrows(IllegalArgumentException.class, () -> {
                deck.withoutTopCards(tooBigSize);
            });
        }
    }

    @Test
    void deckTopCardsReturnsTopCards() {
        var rng = TestRandomizer.newRandom();
        for (int deckSize = 0; deckSize < 20; deckSize++) {
            var cards = SortedBag.of(deckSize, "card");
            var deck = Deck.of(cards, rng);
            assertEquals(cards, deck.topCards(deckSize));
        }
    }

    @Test
    void deckWithoutTopCardsRemovesTopCards() {
        var cards = SortedBag.of(listOfSize((1 << 8) - 1));
        var deck = Deck.of(cards, TestRandomizer.newRandom());
        var actualCardsBuilder = new SortedBag.Builder<Integer>();
        for (int i = 0; i < 8; i++) {
            var count = 1 << i; // == 2^i
            actualCardsBuilder.add(deck.topCards(count));
            deck = deck.withoutTopCards(count);
        }
        assertTrue(deck.isEmpty());
        assertEquals(cards, actualCardsBuilder.build());
    }

    private static <E extends Comparable<E>> List<E> deckToList(Deck<E> deck) {
        var list = new ArrayList<E>(deck.size());
        while (!deck.isEmpty()) {
            var topCard = deck.topCard();
            list.add(topCard);
            deck = deck.withoutTopCard();
        }
        return Collections.unmodifiableList(list);
    }

    private static List<Integer> listOfSize(int size) {
        var list = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) list.add(i);
        return Collections.unmodifiableList(list);
    }
}
