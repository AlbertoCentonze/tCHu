package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {

    @Test
    void builderConstructorFailsWithNegativeArgument(){
        IntStream.range(-10, 0).forEach((n) -> {
            assertThrows(IllegalArgumentException.class, () -> {
                new StationPartition.Builder(n);
            });
        });
    }

    @Test
    void builderWorksCorrectly(){
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        List<Station> stations = ChMap.stations();
        StationPartition initialPartition = builder.build();

        for (int i = 0; i < stations.size() - 1; ++i){
            builder.connect(stations.get(i), stations.get(i + 1));
        }
        StationPartition partition = builder.build();
        System.out.println(Arrays.toString(partition.stationsInPartition));
    }

    @Test
    void BuilderWorksWithoutConnectingAnyStation(){
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        StationPartition partition = builder.build();
        for (Station s1: ChMap.stations()){
            for (Station s2: ChMap.stations()){
                if (s1.id() == s2.id()){
                    assertTrue(partition.connected(s1, s2));
                }else{
                    assertFalse(partition.connected(s1, s2));
                }
            }
        }
    }

    @Test
    void BuilderWorksWithStationsOutOfBound(){
        List<Station> stations = ChMap.stations().subList(0, 15);
        StationPartition.Builder builder = new StationPartition.Builder(15);
        //what if connect(19, 5)? with max 15
    }
}
