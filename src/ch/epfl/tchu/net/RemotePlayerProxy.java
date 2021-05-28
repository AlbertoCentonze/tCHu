package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Emma Poggiolini (330757)
 * Proxy of the Remote Player
 */
public final class RemotePlayerProxy implements Player {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * RemotePlayerProxy constructor, connecting the proxy to the client to exchange messages
     * @param socket : socket used by the proxy to communicate with the client through the network
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void sendMessage(String message){
        try {
            writer.write(message);
            writer.write("\n");
            writer.flush();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage(){
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @see Player#initPlayers(PlayerId, Map)
     */
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> listOfNames = new ArrayList<>(playerNames.values());
        String message = String.join(" ",
                MessageId.INIT_PLAYERS.name(),
                Serdes.PLAYER_ID_SERDE.serialize(ownId),
                Serdes.LIST_OF_STRING_SERDE.serialize(listOfNames));
        sendMessage(message);
    }

    /**
     * @see Player#receiveInfo(String)
     */
    public void receiveInfo(String info) {
        String message = String.join(" ",
                MessageId.RECEIVE_INFO.name(),
                Serdes.STRING_SERDE.serialize(info));
        sendMessage(message);
    }

    /**
     * @see Player#updateState(PublicGameState, PlayerState)
     */
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String message = String.join(" ",
                MessageId.UPDATE_STATE.name(),
                Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                Serdes.PLAYER_STATE_SERDE.serialize(ownState));
        sendMessage(message);
    }

    /**
     * @see Player#setInitialTicketChoice(SortedBag)
     */
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String message = String.join(" ",
                MessageId.SET_INITIAL_TICKETS.name(),
                Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(tickets));
        sendMessage(message);
    }

    /**
     * @see Player#chooseInitialTickets()
     */
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#nextTurn()
     */
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN.name());
        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#chooseTickets(SortedBag)
     */
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String message = String.join(" ",
                MessageId.CHOOSE_TICKETS.name(),
                Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(options));
        sendMessage(message);
        return Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#drawSlot()
     */
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());
        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#claimedRoute()
     */
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#initialClaimCards()
     */
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());
        return Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(receiveMessage());
    }

    /**
     * @see Player#chooseAdditionalCards(List)
     */
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String message = String.join(" ",
                MessageId.CHOOSE_ADDITIONAL_CARDS.name(),
                Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.serialize(options));
        sendMessage(message);
        return Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(receiveMessage());
    }
}
