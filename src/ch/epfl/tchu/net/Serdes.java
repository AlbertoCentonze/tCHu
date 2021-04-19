package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Serdes {
    private Serdes() {} //TODO

    public static final Serde<Integer> INTEGER_SERDE = Serde.of(n -> Integer.toString(n), Integer::parseInt);
    public static final Serde<String> STRING_SERDE = Serde.of(
                    // encoding function
                    string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
                    // decoding function
                    encodedString -> Arrays.toString(Base64.getDecoder().decode(encodedString)));
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());
    public static final Serde<List<String>> LIST_OF_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");
    //TODO add the missing serdes
}
