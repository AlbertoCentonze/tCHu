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

public class RemotePlayerClient {
    BufferedReader reader;
    BufferedWriter writer;
    Socket socket;
    Player player;

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

    public void run() {
        // quasi-infinite loop: the game lasts for maximum 1000 turns
        int turn = 0;
        // while (turn < 1000) { // TODO while(true) ?
            try {
                String[] split = reader.readLine().split(Pattern.quote(" "), -1);
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
                    case RECEIVE_INFO: // TODO assert that the size of each split is correct
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
                        break;
                    case NEXT_TURN:
                        writer.write(Serdes.TURN_KIND_SERDE.serialize(player.nextTurn()));
                        break;
                    case CHOOSE_TICKETS:
                        writer.write(Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(
                                player.chooseTickets(Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(split[1]))));
                        break;
                    case DRAW_SLOT:
                        writer.write(Serdes.INTEGER_SERDE.serialize(player.drawSlot()));
                        break;
                    case ROUTE:
                        writer.write(Serdes.ROUTE_SERDE.serialize(player.claimedRoute()));
                        break;
                    case CARDS:
                        writer.write(Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(player.initialClaimCards()));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        writer.write(Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(
                                player.chooseAdditionalCards(Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.deserialize(split[1]))));
                        break;
                }
                writer.write("\n");
                writer.flush();
                ++turn;
            } catch (IOException e) {
                e.printStackTrace(); // TODO automatic by IntelliJ
            }
       // }
    }
}
