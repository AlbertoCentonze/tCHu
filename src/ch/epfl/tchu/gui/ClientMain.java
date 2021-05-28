package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author Alberto Centonze (327267)
 * Game Client
 */
public final class ClientMain extends Application {
    /**
     * Run the program
     * @param args : parameters under the form of an array of Strings
     */
    public static void main(String[] args) { launch(args); }

    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start(Stage primaryStage) {
        // default ip and port
        String ip = "localhost";
        int port = 5108;
        List<String> params = getParameters().getRaw();
        if(!params.isEmpty()) {
            ip = params.get(0);
            port = Integer.parseInt(params.get(1));
        }
        RemotePlayerClient remotePlayer = new RemotePlayerClient(new GraphicalPlayerAdapter(), ip, port);

        new Thread(remotePlayer::run).start();
    }
}
