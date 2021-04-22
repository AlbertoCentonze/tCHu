package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {
    BufferedReader reader;
    BufferedWriter writer;

    public RemotePlayerClient(String name, int port){
        try {
            Socket s = new Socket(name, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void run(){

    }
}
