package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
            writer.write("\n");
            writer.flush();
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage(){
        try{
            return reader.readLine();
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> listOfNames = new ArrayList<>(playerNames.values());
        String message = String.join(" ",
                MessageId.INIT_PLAYERS.name(),
                Serdes.PLAYER_ID_SERDE.serialize(ownId),
                Serdes.LIST_OF_STRING_SERDE.serialize(listOfNames));
        sendMessage(message);
    }

    @Override
    public void receiveInfo(String info) {
        String message = String.join(" ",
                MessageId.RECEIVE_INFO.name(),
                Serdes.STRING_SERDE.serialize(info));
        sendMessage(message);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String message = String.join(" ",
                MessageId.UPDATE_STATE.name(),
                Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                Serdes.PLAYER_STATE_SERDE.serialize(ownState));
        sendMessage(message);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String message = String.join(" ",
                MessageId.SET_INITIAL_TICKETS.name(),
                Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(tickets));
        sendMessage(message);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN.name());
        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String message = String.join(" ",
                MessageId.CHOOSE_TICKETS.name(),
                Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(options));
        sendMessage(message);
        return Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());
        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());
        return Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String message = String.join(" ",
                MessageId.CHOOSE_ADDITIONAL_CARDS.name(),
                Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.serialize(options));
        sendMessage(message);
        return Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(receiveMessage());
    }
}
