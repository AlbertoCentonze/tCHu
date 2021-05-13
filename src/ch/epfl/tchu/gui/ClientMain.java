package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
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

public final class ClientMain extends Application {
    public static void main(String[] args){ launch(args); }

    public void start(Stage stage){
        Map<PlayerId, String> names = new EnumMap<PlayerId, String>(PlayerId.class);
        names.put(PlayerId.PLAYER_1, "Alberto");
        names.put(PlayerId.PLAYER_2, "Emma");
        String ip = "localhost";
        int port = 5108;
        List<String> params = getParameters().getRaw();
        if (params.size() == 1){
            String[] splitParams = params.get(0).split(":");
            ip = splitParams[0];
            port = Integer.parseInt(splitParams[1]);
        }
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            Player remotePlayer = new RemotePlayerClient(new RemotePlayerProxy(socket), ip, port);
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
        Player localPlayer = new GraphicalPlayerAdapter();
        new Thread(() -> remotePlayer).start();

    }
}
