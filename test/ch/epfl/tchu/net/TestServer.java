package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class TestServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (
            ServerSocket serverSocket = new ServerSocket(5108);
            Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
                var playerNames = Map.of(PLAYER_1, "Alberto",
                    PLAYER_2, "Emma");
            playerProxy.initPlayers(PLAYER_1, playerNames);
        }
        System.out.println("Server done!");
    }
}
