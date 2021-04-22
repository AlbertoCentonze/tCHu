package ch.epfl.tchu.net;

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
    void listOfCardSerdeWorks(){
        
    }
    //TODO add the missing tests
}
