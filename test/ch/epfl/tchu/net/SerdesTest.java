package ch.epfl.tchu.net;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SerdesTest {
    private static <T> void testSerde(List<T> testList, Serde<T> serde){
        Set<String> serializedSet = new HashSet<>();
        Set<T> deserializedSet = new HashSet<>();
        for (T t : testList){
            String serialized = serde.serialize(t);
            serializedSet.add(serialized);
            T deserialized = serde.deserialize(serialized);
            deserializedSet.add(deserialized);
            assertEquals(t, deserialized);
        }
        assertEquals(new HashSet<>(testList).size(), serializedSet.size());
        assertEquals(new HashSet<>(testList), deserializedSet);
    }

    private static List<String> randomStringGenerator(){
        List<String> stringList = new ArrayList<>();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        for (int i = 0; i < 100; ++i){
            StringBuilder sb = new StringBuilder(rnd.nextInt(100));
            for (int n = 0; n < 100; n++)
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            stringList.add(sb.toString());
        }
        return stringList;
    }

    @Test
    void integerSerdeWorks(){
        List<Integer> integerList = IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        testSerde(integerList, Serdes.INTEGER_SERDE);
    }

    @Test
    void stringSerdeWorks(){
        testSerde(randomStringGenerator(), Serdes.STRING_SERDE);
    }

    @Test
    void playerIdSerdeWorks(){
        testSerde(PlayerId.ALL, Serdes.PLAYER_ID_SERDE);
    }

    @Test
    void TurnKindSerdeWorks(){
        testSerde(Player.TurnKind.ALL, Serdes.TURN_KIND_SERDE);
    }

    @Test
    void cardSerdeWorks(){
        testSerde(Card.ALL, Serdes.CARD_SERDE);
    }

    @Test
    void routeSerdeWorks(){
        testSerde(ChMap.routes(), Serdes.ROUTE_SERDE);
    }

    @Test
    void ticketSerdeWorks(){
        testSerde(ChMap.tickets(), Serdes.TICKET_SERDE);
    }

    @Test
    void listOfStringSerdeWorks(){
        List<List<String>> listOfString = new ArrayList<>();
        for (int i = 0; i < 100; ++i)
            listOfString.add(randomStringGenerator());
        testSerde(listOfString, Serdes.LIST_OF_STRING_SERDE);
    }

    @Test
    void listOfStringSerdeWorksWithEmptyList(){
        testSerde(List.of(List.of()), Serdes.LIST_OF_STRING_SERDE);
    }

    @Test
    void listOfCardSerdeWorks(){
        Random rnd = new Random();
        List<List<Card>> listOfCards = new ArrayList<>();
        for (int i = 0; i < 10; ++i){
            int a = rnd.nextInt(Card.ALL.size());
            int b = rnd.nextInt(Card.ALL.size());
            if (a > b){
                int tmp = a;
                a = b;
                b = tmp;
            }
            listOfCards.add(Card.ALL.subList(a, b));
        }
        System.out.println(listOfCards);
        testSerde(listOfCards, Serdes.LIST_OF_CARD_SERDE);
    }
    //TODO add the missing tests
    // TODO add empty list tests

    @Test
    void publicGameSerdeWorksExampleFromClass() {
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);
        // serialized
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs));

        // deserialized
        // gameState ticket count is the same
        assertEquals(gs.ticketsCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").ticketsCount());
        // gameState cardState is the same
        assertEquals(gs.cardState().deckSize(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().deckSize());
        assertEquals(gs.cardState().discardsSize(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().discardsSize());
        assertEquals(gs.cardState().faceUpCard(3), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().faceUpCard(3));
        assertEquals(gs.cardState().faceUpCards(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().faceUpCards());
        // gameState has the same claimed routes
        assertEquals(gs.claimedRoutes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").claimedRoutes());
        // gameState has the same current player id
        assertEquals(gs.currentPlayerId(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").currentPlayerId());
        // gameState has the same last player id
        assertEquals(gs.lastPlayer(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());
        // gameState has the same playerState for PLAYER_1
        assertEquals(gs.playerState(PLAYER_1).carCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).carCount());
        assertEquals(gs.playerState(PLAYER_1).routes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).routes());
        assertEquals(gs.playerState(PLAYER_1).cardCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).cardCount());
        assertEquals(gs.playerState(PLAYER_1).ticketCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).ticketCount());
        assertEquals(gs.playerState(PLAYER_1).claimPoints(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).claimPoints());
        // gameState has the same playerState for PLAYER_2
        assertEquals(gs.playerState(PLAYER_2).carCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).carCount());
        assertEquals(gs.playerState(PLAYER_2).routes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).routes());
        assertEquals(gs.playerState(PLAYER_2).cardCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).cardCount());
        assertEquals(gs.playerState(PLAYER_2).ticketCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).ticketCount());
        assertEquals(gs.playerState(PLAYER_2).claimPoints(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).claimPoints());
    }
}
