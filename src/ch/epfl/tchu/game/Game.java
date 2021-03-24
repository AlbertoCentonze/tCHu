package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import static ch.epfl.tchu.game.Constants.ALL_CARDS;
import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Game {
    private Game(){

    }

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2);
        Preconditions.checkArgument(playerNames.size() == 2);
        GameState game = GameState.initial(tickets, rng);
        Deck<Card> deck = Deck.of(ALL_CARDS, rng);
        Map<PlayerId, PlayerState> playerStates = new HashMap<>();
        for (PlayerId id : players.keySet()) {
            SortedBag<Card> initialCards = deck.topCards(INITIAL_CARDS_COUNT);
            deck = deck.withoutTopCards(INITIAL_CARDS_COUNT);
            PlayerState initialPlayerState = PlayerState.initial(initialCards);
            playerStates.put(id, initialPlayerState);
        }
        playerStates.put(PLAYER_1, );
        playerStates.put(PLAYER_1, PlayerState.initial());
        //Step 1
        players.forEach((id, player) -> player.initPlayers(id, playerNames));
        //Step 2
        Map<PlayerId, Info> info = new HashMap<>();
        info.put(PLAYER_1, new Info(playerNames.get(PLAYER_1)));
        info.put(PLAYER_2, new Info(playerNames.get(PLAYER_2)));
        int firstPlayerIndex = rng.nextInt(2);
        PlayerId firstPlayer = PlayerId.ALL.get(firstPlayerIndex);
        String firstPlayerMessage = info.get(firstPlayer).willPlayFirst();
        players.values().forEach((p) -> p.receiveInfo(firstPlayerMessage));
        //Step 3
        for (Player p : players.values()) {
            p.setInitialTicketChoice(game.topTickets(INITIAL_TICKETS_COUNT));
            game = game.withoutTopTickets(5);
        }
        //TODO Step 4

    }
}
