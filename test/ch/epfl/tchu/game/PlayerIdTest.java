package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerIdTest {
    @Test
    void nextWorksWithPlayer1(){
        assertEquals(PlayerId.PLAYER_2, PlayerId.PLAYER_1.next());
    }

    @Test
    void nextWorksWithPlayer2(){
        assertEquals(PlayerId.PLAYER_1, PlayerId.PLAYER_2.next());
    }
}
