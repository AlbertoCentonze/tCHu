package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Ticket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Emma Poggiolini (330757)
 */

/**
 * Client of Remote Player
 */
public class RemotePlayerClient {
    BufferedReader reader;
    BufferedWriter writer;
    Socket socket;
    Player player;

    /**
     * RemotePlayerClient constructor, connecting the client to the proxy to exchange messages
     * @param player : remote player to whom the access is given
     * @param name : name of the socket used by the proxy to communicate with the client through the network
     * @param port : port of the socket
     */
    public RemotePlayerClient(Player player, String name, int port){
        try {
            socket = new Socket(name, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            this.player = player;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Loop for exchanging messages with the proxy
     * receives messages from the proxy, deserializes them and calls the corresponding methods of the player,
     * serializes the result and sends it back to the proxy
     * @throws UncheckedIOException // TODO when
     */
    public void run() {
        try {
            while(true) {
                String read = reader.readLine(); // TODO put in while
                if(read == null) {
                    break;
                }
                String[] split = read.split(Pattern.quote(" "), -1);
                switch (MessageId.valueOf(split[0])) {
                    case INIT_PLAYERS:
                        // list of deserialized strings of the players' names
                        List<String> names = Serdes.LIST_OF_STRING_SERDE.deserialize(split[2]);
                        // create the map containing the players' names
                        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                        for (int i = 0; i < PlayerId.COUNT; ++i) {
                            playerNames.put(PlayerId.ALL.get(i), names.get(i));
                        }
                        player.initPlayers(Serdes.PLAYER_ID_SERDE.deserialize(split[1]), playerNames);
                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING_SERDE.deserialize(split[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(split[1]),
                                Serdes.PLAYER_STATE_SERDE.deserialize(split[2]));
                        break;
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(split[1]));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        writer.write(Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(player.chooseInitialTickets()));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case NEXT_TURN:
                        writer.write(Serdes.TURN_KIND_SERDE.serialize(player.nextTurn()));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case CHOOSE_TICKETS:
                        writer.write(Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(
                                player.chooseTickets(Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(split[1]))));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case DRAW_SLOT:
                        writer.write(Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case ROUTE:
                        writer.write(Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case CARDS:
                        writer.write(Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(player.initialClaimCards()));
                        writer.write("\n");
                        writer.flush();
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        writer.write(Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(
                                player.chooseAdditionalCards(Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.deserialize(split[1]))));
                        writer.write("\n");
                        writer.flush();
                        break;
                }
            }
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

    }
}
