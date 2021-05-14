package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Emma Poggiolini (330757)
 */

/**
 * Server
 */
public class ServerMain extends Application {
    // map associating the players' ids to the players
    Map<PlayerId,Player> players;

    /**
     * Run the program
     * @param args : parameters under the form of an array of Strings
     */
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        // default player names
        Map<PlayerId,String> names = Map.of(PLAYER_1, "Ada",
                PLAYER_2, "Charles");
        List<String> params = getParameters().getRaw();
        if(params.size() > 0) {
            for (String s : params) {
                names.put(PlayerId.ALL.get(params.indexOf(s)), s);
                System.out.println(s);
            }
        }
        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
                System.out.println("Hello");
            players = Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                    PLAYER_2, new RemotePlayerProxy(socket));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        new Thread(() -> Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random())).start();

    }
}
