package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Serdes { // TODO abstract bc non-instantiable
    private Serdes() {
    } //TODO

    public static final Serde<Integer> INTEGER_SERDE = Serde.of(n -> Integer.toString(n), Integer::parseInt);
    public static final Serde<String> STRING_SERDE = Serde.of(
            // encoding function
            string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            // decoding function // TODO
            // encodedString -> Arrays.toString(Base64.getDecoder().decode(encodedString)));
            encodedString -> new String((Base64.getDecoder().decode(encodedString)), StandardCharsets.UTF_8));

    // Serializer-deserializer of enumerates
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    // Serializer-deserializer of types with a finite number of values
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    // Serializer-deserializer of lists and sorted bags
    public static final Serde<List<String>> LIST_OF_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");
    public static final Serde<List<Card>> LIST_OF_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");
    public static final Serde<List<Route>> LIST_OF_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");
    public static final Serde<SortedBag<Card>> SORTEDBAG_OF_CARD_SERDE = Serde.bagOf(CARD_SERDE, ","); // TODO SortedBags made into Lists first ?
    public static final Serde<SortedBag<Ticket>> SORTEDBAG_OF_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");
    public static final Serde<List<SortedBag<Card>>> LIST_OF_SORTEDBAG_OF_CARD_SERDE = Serde.listOf(SORTEDBAG_OF_CARD_SERDE, ";");

    // Serializer-deserializer of composite types
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            // function to serialize
            publicCardState -> String.join(";", LIST_OF_CARD_SERDE.serialize(publicCardState.faceUpCards()),
                    INTEGER_SERDE.serialize(publicCardState.deckSize()),
                    INTEGER_SERDE.serialize(publicCardState.discardsSize())),
            // function to deserialize
            serializedPublicCardState -> {
                String[] split = serializedPublicCardState.split(Pattern.quote(";"), -1);
                return new PublicCardState(LIST_OF_CARD_SERDE.deserialize(split[0]),
                        INTEGER_SERDE.deserialize(split[1]), INTEGER_SERDE.deserialize(split[2])); }
    );

    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            // function to serialize
            publicPlayerState -> String.join(";", INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
                    INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
                    LIST_OF_ROUTE_SERDE.serialize(publicPlayerState.routes())),
            // function to deserialize
            serializedPublicPlayerState -> {
                String[] split = serializedPublicPlayerState.split(Pattern.quote(";"), -1);
                return new PublicPlayerState(INTEGER_SERDE.deserialize(split[0]),
                        INTEGER_SERDE.deserialize(split[1]), LIST_OF_ROUTE_SERDE.deserialize(split[2])); }
    );

    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            // function to serialize
            playerState -> String.join(";", SORTEDBAG_OF_TICKET_SERDE.serialize(playerState.tickets()),
                    SORTEDBAG_OF_CARD_SERDE.serialize(playerState.cards()),
                    LIST_OF_ROUTE_SERDE.serialize(playerState.routes())),
            // function to deserialize
            serializedPlayerState -> {
                String[] split = serializedPlayerState.split(Pattern.quote(";"), -1);
                return new PlayerState(SORTEDBAG_OF_TICKET_SERDE.deserialize(split[0]),
                        SORTEDBAG_OF_CARD_SERDE.deserialize(split[1]), LIST_OF_ROUTE_SERDE.deserialize(split[2])); }
    );

    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            // function to serialize
            publicGameState -> String.join(":", INTEGER_SERDE.serialize(publicGameState.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()),
                    PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)), // TODO specific to 2 players
                    // if the last player is null, it is serialized as an empty string
                    publicGameState.lastPlayer() == null ? "" :
                            PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer())), // TODO lastPlayer() null --> chaine vide
            // function to deserialize
            serializedPublicGameState -> {
                String[] split = serializedPublicGameState.split(Pattern.quote(":"), -1);
                // create the map containing the public players' states
                Map<PlayerId, PublicPlayerState> playerStates = Map.of();
                for(int i = 0; i < PlayerId.COUNT; ++i) {
                    playerStates.put(PlayerId.ALL.get(i), PUBLIC_PLAYER_STATE_SERDE.deserialize(split[3+i]));
                }
                return new PublicGameState(INTEGER_SERDE.deserialize(split[0]),
                        PUBLIC_CARD_STATE_SERDE.deserialize(split[1]), PLAYER_ID_SERDE.deserialize(split[2]),
                        // if the last string is empty, lastPlayer is set to null
                        playerStates, split[split.length - 1].equals("") ? null :
                        PLAYER_ID_SERDE.deserialize(split[split.length-1])); }
    );

}
