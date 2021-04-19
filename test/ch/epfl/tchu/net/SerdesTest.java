package ch.epfl.tchu.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    void routeSerdeWorks(){
        testSerde(ChMap.routes(), Serdes.ROUTE_SERDE);
    }

    @Test
    void ticketSerdeWorks(){
        testSerde(ChMap.tickets(), Serdes.TICKET_SERDE);
    }

    //TODO add the missing tests
}
