package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

public class RemotePlayerClient {
    public RemotePlayerClient(Player player, String name, int port){
        try{
            Socket socket = new Socket(name, port);
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    public void run(){

    }
}
