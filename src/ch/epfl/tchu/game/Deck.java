package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Random;

import static java.util.Collections.shuffle;

/**
 * @author Emma Poggiolini (330757)
 */

public final class Deck <C extends Comparable<C>> {
    private final List<C> deck;

    /**
     * Constructor internal to Deck
     * @param shuffledDeck
     */
    private Deck(List<C> shuffledDeck){
        deck = List.copyOf(shuffledDeck);
    }

    /**
     * Create a deck with shuffled cards
     * @param cards
     * @param rng
     * @param <C> type
     * @return
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> deck = cards.toList();
        shuffle(deck, rng);
        return new Deck<>(deck);
    }

    /**
     * Return the number of cards in the deck
     * @return (int) size of the deck
     */
    public int size(){
        return deck.size();
    }

    /**
     * Establish whether the deck is empty
     * @return (boolean) true if deck is empty
     */
    public boolean isEmpty(){
        return deck.isEmpty();
    }

    /**
     * Return the card at the top of the deck
     * @return (C) first card
     */
    public C topCard(){
        // check that the deck isn't empty
        Preconditions.checkArgument(!isEmpty());
        return deck.get(0);
    }

    /**
     * Create new deck without the top card
     * @return (Deck<C>) deck without the top card
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        return new Deck<>(deck.subList(1,size()));
    }

    /**
     * Return the first count cards at the top of the deck
     * @param count (number of cards to return)
     * @return (SortedBag<C>) first count cards
     */
    public SortedBag<C> topCards(int count){
        // check count is between 0 and the size of the deck (included)
        Preconditions.checkArgument(count >= 0 && count <= size());
        return SortedBag.of(deck.subList(0, count));
    }

    /**
     * Create new deck without the top cards
     * @return (Deck<C>) deck without the top cards
     */
    public Deck<C> withoutTopCards(int count){
        // check count is between 0 and the size of the deck (included)
        Preconditions.checkArgument(count >= 0 && count <= size());
        return new Deck<>(deck.subList(count,size()));
    }
}
