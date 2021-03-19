package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

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
    void builderWorksCorrectlyWithUniquePartition(){
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        List<Station> stations = ChMap.stations();
        for (int i = 0; i < stations.size() - 1; ++i){
            builder.connect(stations.get(i), stations.get(i + 1));
        }
        StationPartition partition = builder.build();
        for (Station s1 : ChMap.stations()){
            for (Station s2 : ChMap.stations()) {
                assertTrue(partition.connected(s1, s2));
            }
        }
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
        StationPartition partition = new StationPartition.Builder(15).build();
        List<Station> stationsOutOfBounds = ChMap.stations().subList(15, 51);
        for (Station s1 : stationsOutOfBounds){
            for (Station s2 : stationsOutOfBounds){
                if (partition.connected(s1, s2)){
                    assertEquals(s1, s2);
                }
                else {
                    assertNotEquals(s1, s2);
                }
            }
        }
        assertFalse(partition.connected(ChMap.stations().get(15), ChMap.stations().get(16)));
        assertFalse(partition.connected(ChMap.stations().get(16), ChMap.stations().get(15)));
        assertFalse(partition.connected(ChMap.stations().get(7), ChMap.stations().get(16)));
        assertFalse(partition.connected(ChMap.stations().get(16), ChMap.stations().get(7)));
        assertTrue(partition.connected(ChMap.stations().get(15), ChMap.stations().get(15)));
    }

    @Test
    void BuilderWorksConnectingSomeStationsInDifferentOrders(){
        StationPartition.Builder builder1 = new StationPartition.Builder(ChMap.stations().size());
        StationPartition.Builder builder2 = new StationPartition.Builder(ChMap.stations().size());
        int[] pairs= {0, 1, 2, 3, 7, 8, 9, 12, 13, 15, 17, 10};
        for (int i = 0; i < pairs.length; i += 2){
            Station s1 = ChMap.stations().get(pairs[i]);
            Station s2 = ChMap.stations().get(pairs[i + 1]);
            builder1.connect(s1, s2);
            builder2.connect(s2, s1);
        }
        StationPartition partition1 = builder1.build();
        StationPartition partition2 = builder2.build();
        for (int i = 0; i < pairs.length; i += 2){
            Station s1 = ChMap.stations().get(pairs[i]);
            Station s2 = ChMap.stations().get(pairs[i + 1]);
            assertEquals(partition1.connected(s1, s2), partition2.connected(s1, s2));
        }
    }







    // not ours

    private static final Station AT1 = new Station(39, "Autriche");
    private static final Station BER = new Station(3, "Berne");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station STG = new Station(27, "Saint-Gall");
    private static Station BAD = new Station(0, "Baden");
    private static Station ZUR = new Station(33, "Zürich");
    private static Station OLT = new Station(20, "Olten");
    private static Station AT2 = new Station(40, "Autriche");
    private static Station VAD = new Station(28, "Vaduz");
    private static final Station BAL = new Station(1, "Bâle");
    private static final Station BEL = new Station(2, "Bellinzone");
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station BRU = new Station(5, "Brusio");

    //for method id out of bounds
    private static final Station SCZ = new Station(24, "Schwyz");


    @Test
    void connectedWorksForConnected (){
        StationPartition.Builder part1Builder = new StationPartition.Builder(17);
        part1Builder.connect(BER, LUC);
        StationPartition part1 = part1Builder.build();
        assertEquals(true, part1.connected(BER,LUC));
    }

    @Test
    void connectedWorksForMultipleConnected () {
        StationPartition.Builder part1Builder = new StationPartition.Builder(34);
        part1Builder.connect(BER, LUC);
        part1Builder.connect(OLT, LUC);
        part1Builder.connect(OLT, ZUR);
        StationPartition part1 = part1Builder.build();
        assertEquals(true, part1.connected(BER,ZUR));
    }

    @Test
    void connectedWorksForNotConnected (){
        StationPartition.Builder part1Builder = new StationPartition.Builder(17);
        StationPartition part1 = part1Builder.build();
        assertEquals(false, part1.connected(BER,LUC));
    }

    @Test
    void builderConstructorPrecondition (){
        assertThrows(IllegalArgumentException.class, () -> {
            new StationPartition.Builder(-1);
        });
    }

    @Test
    public void stationPartitionBuilderTestWithTwoStations(){

        StationPartition.Builder builder = new StationPartition.Builder(17);
        builder.connect(BER, LUC);
        StationPartition something = builder.build();
        assertTrue(something.connected(BER, LUC));

        //sameRepresentative
        int[] liens = something.getLiens();
        assertEquals(liens[16], liens[3]);
        assertEquals(liens[3], liens[16]);
    }

    @Test
    public void connectedStationIdOutOfBoundsOfChartGivenToConstructor(){

        StationPartition.Builder builder = new StationPartition.Builder(17);
        builder.connect(BER, LUC);
        StationPartition something = builder.build();

        //returns false with an id out of bounds
        assertFalse(something.connected(BER, SCZ));
        assertFalse(something.connected(BER, ZUR));
        assertFalse(something.connected(LUC, SCZ));
        assertFalse(something.connected(LUC, ZUR));

        //both stations with id out of bounds
        assertFalse(something.connected(ZUR, SCZ));

        //returns true iff both stations have the same id
        assertTrue(something.connected(SCZ, SCZ));
    }

    @Test
    public void connectedReturnsFalseIfNotConnected(){
        StationPartition.Builder builder = new StationPartition.Builder(17);
        builder.connect(BER, LUC);
        StationPartition something = builder.build();
        assertFalse(something.connected(BER, BAD));
        assertFalse(something.connected(LUC, BAD));
    }

    //index
    @Test
    public void stationPartitionBuilderWithMultipleStations(){

        //connect 7 stations
        StationPartition.Builder builder = new StationPartition.Builder(17);
        builder.connect(BER, LUC);
        builder.connect(BER, BAD);
        builder.connect(BAL, BEL);
        builder.connect(BAL, BAD);
        builder.connect(BER, BRI);
        builder.connect(BAD, BRU);
        StationPartition something = builder.build();
        List<Station> stations = List.of(BER, LUC, BAD, BAL, BEL, BRI, BRU);

        //methodConnect
        for(Station station : stations){
            for(Station s : stations){
                assertTrue(something.connected(station, s));
            }
        }

        //same representative
        int[] liens = something.getLiens();

        for(int i=0; i<6; i++){
            assertEquals(liens[i], liens[0]);
            assertEquals(liens[i], liens[1]);
            assertEquals(liens[i], liens[2]);
            assertEquals(liens[i], liens[3]);
            assertEquals(liens[i], liens[4]);
            assertEquals(liens[i], liens[5]);
            assertEquals(liens[i], liens[16]);
        }
        for (int i = 16; i<17; i++){
            assertEquals(liens[i], liens[0]);
            assertEquals(liens[i], liens[1]);
            assertEquals(liens[i], liens[2]);
            assertEquals(liens[i], liens[3]);
            assertEquals(liens[i], liens[4]);
            assertEquals(liens[i], liens[5]);
            assertEquals(liens[i], liens[16]);
        }

        for(int i = 6; i<16; i++){
            assertEquals(liens[i], liens[i]);
        }
    }
}
