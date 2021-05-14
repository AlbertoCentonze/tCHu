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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class ServerMain extends Application {
    Map<PlayerId,Player> players;

    public static void main(String[] args) { launch(args); }

    public void start(Stage primaryStage) {
        Map<PlayerId,String> names = Map.of(PLAYER_1, "Alberto",
                PLAYER_2, "Emma");
        List<String> params = getParameters().getRaw();
        if(params.size() > 0) {
            for(PlayerId p : PlayerId.ALL) {
                names.replace(p, params.get(p.ordinal())); // TODO how do I test this ???
            }
        }
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            players = Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                    PLAYER_2, new RemotePlayerProxy(socket));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        new Thread(() -> Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random())).start();

    }
}
