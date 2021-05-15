package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author Alberto Centonze (327267)
 * Game client
 */
public final class ClientMain extends Application {

    /**
     * Run the program
     * @param args : parameters under the form of an array of Strings
     */
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        // default ip and port
        String ip = "localhost";
        int port = 5108;
        List<String> params = getParameters().getRaw();
        if (params.size() == 1) {
            String[] splitParams = params.get(0).split(":");
            ip = splitParams[0];
            port = Integer.parseInt(splitParams[1]);
        } else if(params.size() >= 2) {
            ip = params.get(0);
            port = Integer.parseInt(params.get(1));
        }
        RemotePlayerClient remotePlayer = new RemotePlayerClient(new GraphicalPlayerAdapter(), ip, port);

        new Thread(remotePlayer::run).start();
    }
}
