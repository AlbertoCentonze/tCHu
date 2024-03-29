package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Alberto Centonze (327267)
 * Flattened station partition
 * group of stations that are connected in the player's route network
 */
public final class StationPartition implements StationConnectivity {
    // array of indices of the stations that form the partition
    private final int[] stationsInPartition;

    /**
     * Private constructor of StationPartition
     * @param stationsInPartition : the list with each element of the partition linked to its representative
     */
    private StationPartition(int[] stationsInPartition){
        this.stationsInPartition = Arrays.copyOf(stationsInPartition, stationsInPartition.length);
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        int maxId = Math.max(s1.id(), s2.id());
        if (stationsInPartition.length <= maxId){
            return s1.id() == s2.id();
        }
        return stationsInPartition[s1.id()] == stationsInPartition[s2.id()];
    }

    /**
     * Builder of a station partition
     * creates the deep station partition and flattens it before building it
     */
    public final static class Builder{
        // array of indices of the stations that form the partition
        private final int[] stationsInPartition;

        /**
         * Constructor for the station partition builder
         * @param stationCount : number of stations
         * @throws IllegalArgumentException if the number of stations is negative
         */
        public Builder(int stationCount){
            // check that the number of stations is non-negative
            Preconditions.checkArgument(stationCount >= 0);
            // fill the array with (int) stationCount integers corresponding to the indices
            stationsInPartition =  IntStream.range(0, stationCount).toArray();
        }

        /**
         * Finds the id of the representative of the partition
         * @param id : id of the station of which you want to find the partition
         * @return (int) id of the representative
         */
        private int representative(int id){
            if (stationsInPartition[id] == id)
                return id;
            // recursive method
            return representative(stationsInPartition[id]);
        }

        /**
         * Connects two station (adds one of them to the partition of the other)
         * @param s1 : first station to connect
         * @param s2 : second station to connect
         * @return (StationPartition.Builder) new instance of the builder
         * with the two stations connected between them
         */
        public Builder connect(Station s1, Station s2){
            stationsInPartition[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * Builds the instance of StationPartition with the connected stations
         * @return (StationPartition) new instance of the class StationPartition
         */
        public StationPartition build(){
            //converts the "deep array" into the "plain" one
            int[] stationsInPartitionBuilt = Arrays
                    .stream(stationsInPartition)
                    .map(this::representative)
                    .toArray();
            return new StationPartition(stationsInPartitionBuilt);
        }
    }
}
