package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    /**
     * CardState internal constructor
     * @param topCards
     */
    private CardState(List<Card> topCards, Deck<Card> deck, SortedBag<Card> discards){
        super(topCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    /**
     * Static CardState "builder" that returns a CardState instance with
     * the first five cards as the face-up cards and an empty discards SortedBag
     * @param deck a Deck instance with more than 5 cards
     * @return the instance of CardState built as in the description
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size() >= 5);
        List<Card> topCards = deck.topCards(5).toList();
        Deck<Card> actualDeck = deck.withoutTopCards(5);
        SortedBag<Card> discards = SortedBag.of();
        return new CardState(topCards, actualDeck, discards);
    }

    /**
     * Creates a new CardState instance with a card in the
     * face-up cards list replaced with one from the deck
     * @param slot the index (between 0 and 5) of the card to replace
     * @return the instance of CardState built as in the description
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(this.deck.size() > 0);
        int index = Objects.checkIndex(slot, this.faceUpCards().size());
        //TODO is the deep copy necessary ?
        Deck withoutTopCard = this.deck.withoutTopCard();
        List<Card> topCards = new ArrayList<>(this.faceUpCards());
        topCards.set(slot, this.deck.topCard());
        return new CardState(topCards, withoutTopCard, this.discards);
    }

    /**
     * Getter for the card on the top of the deck (if not empty)
     * @return the Card instance of the first card in the deck
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(this.deck.size() > 0);
        return this.deck.topCard();
    }

    /**
     * Creates a new CardState instance with the first card in the
     * deck removed
     * @return the instance of CardState built as in the description
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(this.deck.size() > 0);
        return new CardState(this.faceUpCards(), this.deck.withoutTopCard(), this.discards);
    }

    /**
     * Recreates the deck starting from the card in discards
     * @param rng the random generator used to shuffle the deck
     * @return the instance of CardState built as in the description
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.deck.size() == 0);
        List<Card> listFromDiscards = this.discards.toList();
        Deck<Card> newShuffledDeck = Deck.of(SortedBag.of(listFromDiscards), rng);
        return new CardState(this.faceUpCards(), newShuffledDeck, SortedBag.<Card>of());
    }

    /**
     * Creates a new CardState instance with additionalDiscards added
     * to the discards SortedBag
     * @param additionalDiscards the card to add to discards
     * @return the instance of CardState built as in the description
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(this.faceUpCards(), this.deck, this.discards.union(additionalDiscards));
    }
}
