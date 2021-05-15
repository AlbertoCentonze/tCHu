package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * @author Alberto Centonze (327267)
 * State of the cards that are not owned by the players
 * represents the public card information that is known to all the players
 */
public class PublicCardState {
    // number of discarded cards
    private final int discardsSize;
    // number of cards in the deck
    private final int deckSize;
    // cards turned face-up
    private final List<Card> faceUpCards;

    /**
     * PublicCardState constructor
     * @param faceUpCards : list containing the cards that are visible
     * @param deckSize : the the number of cards deck
     * @param discardsSize : the size of discarded cards
     * @throws IllegalArgumentException if the faceUpCards aren't 5
     * @throws IllegalArgumentException if the deck and discard pile sizes are negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        // check that the visible cards are 5
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        // check that the deck and discard pile sizes are non-negative
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);
        // defensive copy of faceUpCards
        this.faceUpCards = List.copyOf(faceUpCards);
        this.discardsSize = discardsSize;
        this.deckSize = deckSize;
    }

    /**
     * Getter for the face-up cards, which any player can take
     * @return (List<Card>) list containing the visible cards
     */
    public List<Card> faceUpCards(){ return faceUpCards; }

    /**
     * Returns the face-up card at the given index
     * @param slot : the index (between 0 and 5 included) of the desired card
     * @return (Card) card corresponding to the specified index
     */
    public Card faceUpCard(int slot){
        int index = Objects.checkIndex(slot, faceUpCards.size());
        return faceUpCards.get(index);
    }

    /**
     * Getter for the size of the deck
     * @return (int) the size of the deck
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     * Checks whether the deck is empty
     * @return (boolean) true if the deck is empty, false otherwise
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Getter for the size of the discarded cards
     * @return (int) the number of discarded cards
     */
    public int discardsSize(){
        return discardsSize;
    }
}
