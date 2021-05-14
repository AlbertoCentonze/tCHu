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

    public static void main(String[] args) { launch(args); }

    public void start(Stage primaryStage) {
        // default ip and port
        String ip = "localhost";
        int port = 5108;
        List<String> params = getParameters().getRaw();
        if (params.size() == 1) {
            String[] splitParams = params.get(0).split(":");
            ip = splitParams[0];
            port = Integer.parseInt(splitParams[1]);
        } else if(params.size() == 2) {
            ip = params.get(0);
            port = Integer.parseInt(params.get(1));
        }
        RemotePlayerClient remotePlayer = new RemotePlayerClient(new GraphicalPlayerAdapter(), ip, port);

        new Thread(remotePlayer::run).start();
    }
}
