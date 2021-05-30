package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
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
 * Game launcher
 * @author Alberto Centonze (327267)
 * @author Emma Poggiolini (330757)
 */
public class GameManager {
    private Thread host;
    public void launchServer(Map<PlayerId, String> names) {
        host = new Thread(() ->{
            Map<PlayerId,Player> players;
            try (ServerSocket serverSocket = new ServerSocket(5108)){
                Socket socket = serverSocket.accept();
                 players = Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                        PLAYER_2, new RemotePlayerProxy(socket));
            }catch (IOException e){
                throw new UncheckedIOException(e);
            }
            Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random());
            });
        host.start();
    }

    public void killServer(){
        if (host.isAlive()){
            host.interrupt();
        }
    }

    public static void launchRemote(String ipAndPort) { // TODO how to handle defaults?
        String ip = "localhost";
        int port = 5108;
        if (!ipAndPort.isEmpty()){
            String[] splitParams = ipAndPort.split(":");
            ip = splitParams[0];
            port = Integer.parseInt(splitParams[1]);
        }
        RemotePlayerClient remotePlayer = new RemotePlayerClient(new GraphicalPlayerAdapter(), ip, port);

        new Thread(remotePlayer::run).start();
    }

    public static void launchLocal(PlayerAI ai, Map<PlayerId, String> names){
        // TODO throw the correct error if PlayerTypeIsNotCpu
        Map<PlayerId, Player> players = Map.of(
                PLAYER_1, PlayerType.HOST.getPlayer(),
                PLAYER_2, ai);
        new Thread(() -> Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random())).start();
    }
}
