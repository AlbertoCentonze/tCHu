package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PublicCardStateTest2 {
    @Test
    void ErrorCheck(){
        var cards = new DeckTest2.ChCards();
        CardState cS = CardState.of(cards.rDeck);
        PublicCardState pS = new PublicCardState(cS.faceUpCards(),cS.deckSize(),cS.discardsSize());
        CardState cS1 = CardState.of(cards.fDeck);
        PublicCardState pS1 = new PublicCardState(cS1.faceUpCards(),cS1.deckSize(),cS1.discardsSize());
        PublicCardState pS2 = new PublicCardState(cS1.faceUpCards(),0,0);
        pS2.faceUpCard(0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            pS2.faceUpCard(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            pS2.faceUpCard(Constants.FACE_UP_CARDS_COUNT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PublicCardState pS3 = new PublicCardState(cS.faceUpCards().subList(0,2),cS.deckSize(),cS.discardsSize());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PublicCardState pS3 = new PublicCardState(cS.faceUpCards(),-1,cS.discardsSize());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PublicCardState pS3 = new PublicCardState(cS.faceUpCards(),cS.deckSize(),-1);
        });

    }
    @Test
    void initTest(){
        var cards = new DeckTest2.ChCards();
        CardState cS = CardState.of(cards.rDeck);
        cS = cS.withDrawnFaceUpCard(4);
        cS = cS.withMoreDiscardedCards(cards.cards2);
        PublicCardState pS = new PublicCardState(cS.faceUpCards(),cS.deckSize(),cS.discardsSize());
        assertEquals(133, pS.totalSize());
        assertFalse(pS.isDeckEmpty());
        assertEquals(5, pS.faceUpCards().size());
    }
}