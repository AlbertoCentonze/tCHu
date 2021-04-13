package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Alberto Centonze (327267)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2;

    // A list containing all the players
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    // The number of players in game
    public static final int COUNT = ALL.size();

    /**
     * Get the next turn's player
     * @return the corresponding PlayerId instance
     */
    public PlayerId next(){
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}
