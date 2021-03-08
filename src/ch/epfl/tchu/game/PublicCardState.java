package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PublicCardState {
    private final int discardsSize;
    private final int deckSize;
    private final List<Card> faceUpCards; //TODO check immutability in other classes

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == 5);
        Preconditions.checkArgument(deckSize >= 0 || discardsSize >= 0);
        //TODO check nonNull faceUpCards?
        //TODO what if less elements faceUpCards
        this.faceUpCards = faceUpCards;
        this.discardsSize = discardsSize;
        this.deckSize = deckSize;
    }

    public int totalSize(){
        return faceUpCards.size() + this.deckSize + this.discardsSize;
    }

    public List<Card> faceUpCards(){
        return new ArrayList<>(this.faceUpCards);
    }

    public Card faceUpCard(int slot){
        int index = Objects.checkIndex(slot, this.faceUpCards.size());
        return this.faceUpCards.get(index);
    }

    public int deckSize(){
        return deckSize;
    }

    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    public int discardsSize(){
        return discardsSize;
    }
}
