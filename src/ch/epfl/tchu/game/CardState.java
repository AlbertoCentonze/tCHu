package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class CardState {
    private Deck<Card> deck; //pioche
    private List<SortedBag<Card>> discards; //TODO multiensemle?

    private CardState(Deck<Card> deck){
        this.discards = null;
        this.deck = null;
    }

    private CardState(SortedBag<Card> additionalDiscards){
        this.discards = new ArrayList<>(); //TODO
    }

    public static CardState of(Deck<Card> deck){
        deck.topCards(5);
        return null; //TODO
    }

    public CardState withDrawnFaceUpCard(int slot){
        return null; //TODO
    }

    public Card topDeckCard(){
        return null; //TODO
    }

    public CardState withoutTopDeckCard(){
        return null; //TODO
    }

    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.deck.size() == 0);
        return null; //TODO
    }

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return null; //TODO
    }
}
