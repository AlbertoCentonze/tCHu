package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

public final class CardState {
    private CardState(){

    }

    public static CardState of(Deck<Card> deck){
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

    public CardState withDeckRecreateFromDiscard(Random rng) {
        return null; //TODO
    }

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return null; //TODO
    }
}
