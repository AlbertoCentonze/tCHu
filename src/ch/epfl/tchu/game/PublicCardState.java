package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PublicCardState {
    private final int discardsSize;
    private final int deckSize;
    private final List<Card> faceUpCards; //TODO check immutability in other classes

    /**
     * PublicCardState default constructor
     *
     * @param faceUpCards list containing the cards that are visible
     * @param deckSize the the number of cards deck
     * @param discardsSize the size of discarded cards
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == 5);
        Preconditions.checkArgument(deckSize >= 0 || discardsSize >= 0);
        this.faceUpCards = faceUpCards;
        this.discardsSize = discardsSize;
        this.deckSize = deckSize;
    }

    /**
     * The sum of all the cards in the game that are not in the hand of a player yet
     * @return the total number of cards
     */
    public int totalSize(){
        return faceUpCards.size() + this.deckSize + this.discardsSize;
    }

    /**
     * Getter for the face-up cards that every player can take
     * @return a list containing the public cards
     */
    public List<Card> faceUpCards(){
        return new ArrayList<>(this.faceUpCards);
    }

    /**
     * Gets one of the face-up cards
     * @param slot the index (between 0 and 5 included) of the desired card
     * @return the card corresponding to the specified index
     */
    public Card faceUpCard(int slot){
        int index = Objects.checkIndex(slot, this.faceUpCards.size());
        return this.faceUpCards.get(index); //TODO defensive copy?
    }

    /**
     * Getter for deckSize
     * @return the size of the deck
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     * Checks whether the deck is empty
     * @return true if the deck is empty, false otherwise
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Getter for discardedSize
     * @return the number of discarded cards
     */
    public int discardsSize(){
        return discardsSize;
    }
}
