package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {
    BufferedReader reader;
    BufferedWriter writer;

    public RemotePlayerProxy(Socket socket){
        try{
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    private void sendMessage(String message){
        try{
            writer.write(message);
            writer.write(System.lineSeparator()); //TODO use line separator ?
            writer.flush();
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

    }

    @Override
    public void receiveInfo(String info) {

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {

    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return null;
    }

    @Override
    public TurnKind nextTurn() {
        return null;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        return null;
    }

    @Override
    public int drawSlot() {
        return 0;
    }

    @Override
    public Route claimedRoute() {
        return null;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        return null;
    }
}
