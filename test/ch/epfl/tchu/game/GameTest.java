package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Game;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class GameTest {
    private static Map<PlayerId, String> getPlayerNames(){
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        playerNames.put(PlayerId.PLAYER_1, "Alberto");
        playerNames.put(PlayerId.PLAYER_2, "Emma");
        return playerNames;
    }

    private static Map<PlayerId, Player> getPlayers(){
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, new TestPlayer(new Random().nextInt(100000), ChMap.routes()));
        players.put(PlayerId.PLAYER_2, new TestPlayer(new Random().nextInt(32477809), ChMap.routes()));
        return players;
    }

    @Test
    void test(){
        IntStream.range(0, 1).forEach(n ->
                Game.play(getPlayers(), getPlayerNames(), SortedBag.of(ChMap.tickets()), new Random())
                );
    }
}
