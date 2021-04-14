package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;

/**
 * @author Alberto Centonze (327267)
 */

public final class CardState extends PublicCardState {
    // deck of cards
    private final Deck<Card> deck;
    // pile of discarded cards
    private final SortedBag<Card> discards;

    /**
     * CardState internal constructor
     * @param faceUpCards : 5 visible cards
     * @param deck : deck of cards
     * @param discards : discarded cards
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards){
        super(faceUpCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    /**
     * Static CardState "builder" that returns a CardState instance with
     * the first five cards as the face-up cards and an empty discards SortedBag
     * @param deck : Deck instance with more than 5 cards
     * @return (CardState) : new instance of CardState
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size() >= 5);
        // draw the first 5 cards from the deck; they constitute the face-up cards
        List<Card> faceUpCards = new ArrayList<>();
        // deck without 5 top cards after for-loop
        for(int slot : FACE_UP_CARD_SLOTS) {
            faceUpCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        // empty discard pile
        SortedBag<Card> discards = SortedBag.of();
        return new CardState(faceUpCards, deck, discards);
    }

    /**
     * Creates a new CardState instance with a card in the
     * face-up cards list replaced with one from the deck
     * @param slot : index (between 0 and 5) of the card to replace
     * @return (CardState) : new instance of CardState
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!isDeckEmpty());
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        // new Deck from which the top card has been drawn
        Deck<Card> withoutTopCard = deck.withoutTopCard();
        // copy of faceUpCards
        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        // replace the card at the specified slot with the top card from the deck
        faceUpCards.set(slot, deck.topCard());
        return new CardState(faceUpCards, withoutTopCard, discards);
    }

    /**
     * Getter for the card on the top of the deck (if not empty)
     * @return (Card) : first card in the deck
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * Creates a new CardState instance with the first card in the
     * deck removed
     * @return (CardState) : new instance of CardState
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discards);
    }

    /**
     * Recreates the deck starting from the card in discards
     * @param rng : random generator used to shuffle the deck
     * @return (CardState) : new instance of CardState
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        // reshuffle the discard pile to create the new deck
        // discard pile is now empty
        return new CardState(faceUpCards(), Deck.of(discards,rng), SortedBag.of());
    }

    /**
     * Creates a new CardState instance with additionalDiscards added
     * to the discards SortedBag
     * @param additionalDiscards : cards to add to discards
     * @return (CardState) : new instance of CardState
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(faceUpCards(), deck, discards.union(additionalDiscards));
    }
}
