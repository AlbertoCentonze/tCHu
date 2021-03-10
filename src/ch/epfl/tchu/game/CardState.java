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

    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size() >= 5);
        List<Card> topCards = deck.topCards(5).toList();
        Deck<Card> actualDeck = deck.withoutTopCards(5);
        SortedBag<Card> discards = SortedBag.of();
        return new CardState(topCards, actualDeck, discards);
    }

    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(this.deck.size() > 0);
        int index = Objects.checkIndex(slot, this.faceUpCards().size());
        //TODO is the deep copy necessary ?
        Deck withoutTopCard = this.deck.withoutTopCard();
        List<Card> topCards = new ArrayList<>(this.faceUpCards());
        topCards.set(slot, this.deck.topCard());
        return new CardState(topCards, withoutTopCard, this.discards);
    }

    public Card topDeckCard(){
        Preconditions.checkArgument(this.deck.size() > 0);
        return this.deck.topCard();
    }

    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(this.deck.size() > 0);
        return new CardState(this.faceUpCards(), this.deck.withoutTopCard(), this.discards);
    }

    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.deck.size() == 0);
        List<Card> listFromDiscards = this.discards.toList();
        Deck<Card> newShuffledDeck = Deck.of(SortedBag.of(listFromDiscards), rng);
        return new CardState(this.faceUpCards(), newShuffledDeck, SortedBag.<Card>of());
    }

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(this.faceUpCards(), this.deck, this.discards.union(additionalDiscards));
    }
}
