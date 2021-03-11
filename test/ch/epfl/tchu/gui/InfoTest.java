package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {
    private final Info i = new Info("Noah");

    // TODO cardNameWorksWithNoCards ? or int < 0 ?

    @Test
    void cardNameWorksWithOneCard() {
        assertEquals("noire", Info.cardName(Card.BLACK, 1));
    }

    //@Test
    void cardNameWorksWithOneCard2() {
        assertEquals("verte", Info.cardName(Card.GREEN, 1));
    }

    //@Test
    void cardNameWorksWithOneCard3() {
        assertEquals("locomotive", Info.cardName(Card.LOCOMOTIVE, 1));
    }

    @Test
    void cardNameWorksWithMultipleCards() {
        assertEquals("bleues", Info.cardName(Card.BLUE, 3));
    }

    @Test
    void drawWorks() { assertEquals("\nAlberto et Emma sont ex æqo avec 50 points !\n", Info.draw(List.of("Alberto", "Emma"),50)); }

    @Test
    void willPlayFirstWorks() { assertEquals("Noah jouera en premier.\n\n", i.willPlayFirst()); }

    @Test
    void keptTicketsWorksMultiple() { assertEquals("Noah a gardé 3 billets.\n", i.keptTickets(3)); }

    @Test
    void keptTicketsWorksSingle() { assertEquals("Noah a gardé 1 billet.\n", i.keptTickets(1)); }

    @Test
    void canPlayWorks() { assertEquals("\nC'est à Noah de jouer.\n", i.canPlay());}

    @Test
    void drewTicketsWorks() { assertEquals("Noah a tiré 3 billets...\n", i.drewTickets(3));}

    @Test
    void drewBlindCardWorks() { assertEquals("Noah a tiré une carte de la pioche.\n", i.drewBlindCard());}

    @Test
    void drewVisibleCardWorks() { assertEquals("Noah a tiré une carte rouge visible.\n", i.drewVisibleCard(Card.RED));}

}
