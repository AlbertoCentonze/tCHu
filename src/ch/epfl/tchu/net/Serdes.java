package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Emma Poggiolini (330757)
 * Serdes of every type of information transmitted in the messages exchanged between players
 */
public final class Serdes {
    // non-instantiable class
    private Serdes() { throw new UnsupportedOperationException(); }

    public static final Serde<Integer> INTEGER_SERDE = Serde.of(n -> Integer.toString(n), Integer::parseInt);
    public static final Serde<String> STRING_SERDE = Serde.of(
            // encoding function
            string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            // decoding function
            encodedString -> new String((Base64.getDecoder().decode(encodedString)), StandardCharsets.UTF_8));

    // Serializer-deserializer of enumerates
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    // Serializer-deserializer of types with a finite number of values
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    // Serializer-deserializer of lists and sorted bags
    public static final Serde<List<String>> LIST_OF_STRING_SERDE = Serde.listOf(STRING_SERDE, ',');
    public static final Serde<List<Card>> LIST_OF_CARD_SERDE = Serde.listOf(CARD_SERDE, ',');
    public static final Serde<List<Route>> LIST_OF_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ',');
    public static final Serde<SortedBag<Card>> SORTEDBAG_OF_CARD_SERDE = Serde.bagOf(CARD_SERDE, ',');
    public static final Serde<SortedBag<Ticket>> SORTEDBAG_OF_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ',');
    public static final Serde<List<SortedBag<Card>>> LIST_OF_SORTEDBAG_OF_CARD_SERDE = Serde.listOf(SORTEDBAG_OF_CARD_SERDE, ';');

    // Serializer-deserializer of composite types
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            Serdes::publicCardStateSerializer, Serdes::publicCardStateDeserializer);

    private static String publicCardStateSerializer(PublicCardState state){
        return String.join(";", LIST_OF_CARD_SERDE.serialize(state.faceUpCards()),
                INTEGER_SERDE.serialize(state.deckSize()),
                INTEGER_SERDE.serialize(state.discardsSize()));
    }

    private static PublicCardState publicCardStateDeserializer(String serializedState){
        String[] split = serializedState.split(Pattern.quote(";"), -1);
        return new PublicCardState(
            LIST_OF_CARD_SERDE.deserialize(split[0]),
            INTEGER_SERDE.deserialize(split[1]),
            INTEGER_SERDE.deserialize(split[2]));
    }


    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            Serdes::publicPlayerStateSerializer, Serdes::publicPlayerStateDeserializer);

    private static String publicPlayerStateSerializer(PublicPlayerState state){
        return String.join(";",
                INTEGER_SERDE.serialize(state.ticketCount()),
                INTEGER_SERDE.serialize(state.cardCount()),
                LIST_OF_ROUTE_SERDE.serialize(state.routes()));
    }

    private static PublicPlayerState publicPlayerStateDeserializer(String serializedState){
        String[] split = serializedState.split(Pattern.quote(";"), -1);
        return new PublicPlayerState(
                INTEGER_SERDE.deserialize(split[0]),
                INTEGER_SERDE.deserialize(split[1]),
                LIST_OF_ROUTE_SERDE.deserialize(split[2]));
    }


    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            Serdes::playerStateSerializer, Serdes::playerStateDeserializer);

    private static String playerStateSerializer(PlayerState state){
        return String.join(";",
                SORTEDBAG_OF_TICKET_SERDE.serialize(state.tickets()),
                SORTEDBAG_OF_CARD_SERDE.serialize(state.cards()),
                LIST_OF_ROUTE_SERDE.serialize(state.routes()));
    }

    private static PlayerState playerStateDeserializer(String serializedState){
        String[] split = serializedState.split(Pattern.quote(";"), -1);
        return new PlayerState(
                SORTEDBAG_OF_TICKET_SERDE.deserialize(split[0]),
                SORTEDBAG_OF_CARD_SERDE.deserialize(split[1]),
                LIST_OF_ROUTE_SERDE.deserialize(split[2]));
    }


    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            Serdes::publicGameStateSerializer, Serdes::publicGameStateDeserializer);

    private static String publicGameStateSerializer(PublicGameState state){
        return String.join(":",
                INTEGER_SERDE.serialize(state.ticketsCount()),
                PUBLIC_CARD_STATE_SERDE.serialize(state.cardState()),
                PLAYER_ID_SERDE.serialize(state.currentPlayerId()),
                PUBLIC_PLAYER_STATE_SERDE.serialize(state.playerState(PlayerId.PLAYER_1)),
                PUBLIC_PLAYER_STATE_SERDE.serialize(state.playerState(PlayerId.PLAYER_2)),
                // if the last player is null, it is serialized as an empty string
                state.lastPlayer() == null ? "" :
                PLAYER_ID_SERDE.serialize(state.lastPlayer()));
    }


    private static PublicGameState publicGameStateDeserializer(String serializedState){
                    String[] split = serializedState.split(Pattern.quote(":"), -1);
                    // create the map containing the public players' states
                    Map<PlayerId, PublicPlayerState> playerStates = new EnumMap<>(PlayerId.class);
                    for(int i = 0; i < PlayerId.COUNT; ++i) {
                        playerStates.put(PlayerId.ALL.get(i), PUBLIC_PLAYER_STATE_SERDE.deserialize(split[3+i]));
                    }
                    return new PublicGameState(INTEGER_SERDE.deserialize(split[0]),
                            PUBLIC_CARD_STATE_SERDE.deserialize(split[1]), PLAYER_ID_SERDE.deserialize(split[2]),
                            // if the last string is empty, lastPlayer is set to null
                            playerStates, split[split.length - 1].isEmpty() ? null :
                            PLAYER_ID_SERDE.deserialize(split[split.length-1]));
    }

}
