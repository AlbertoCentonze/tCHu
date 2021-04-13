package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Alberto Centonze (327267)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2;

    // list containing all the players
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    // number of players in the game
    public static final int COUNT = ALL.size();

    /**
     * Get the player who will play the next turn
     * or the player other than the current player
     * @return (PlayerId) next player's id
     */
    public PlayerId next(){
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}
