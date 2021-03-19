package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Alberto Centonze
 */
public final class StationPartition implements StationConnectivity {
    private int[] stationsInPartition;

    /**
     * Private constructor of StationPartition
     * @param stationsInPartition the list with each element of the partition linked to its representative
     */
    private StationPartition(int[] stationsInPartition){
        this.stationsInPartition = stationsInPartition;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        int maxId = Math.max(s1.id(), s2.id());
        if (stationsInPartition.length <= maxId){
            return s1.id() == s2.id();
        }
        return stationsInPartition[s1.id()] == stationsInPartition[s2.id()];
    }

    public final static class Builder{
        private int[] stationsInPartition;
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            this.stationsInPartition =  IntStream.range(0, stationCount).toArray();
        }

        /**
         * Finds the id of the representative of the partition
         * @param id of the station of which you want to find the partition
         * @return the id of the representative
         */
        private int representative(int id){
            if (this.stationsInPartition[id] == id)
                return id;
            return representative(stationsInPartition[id]);
        }

        /**
         * Connects two station (basically it adds one of them to the partition of the other station
         * @param s1 the first station to connect
         * @param s2 the second station to connect
         * @return a new instance of the builder with the two stations connected between them
         */
        public Builder connect(Station s1, Station s2){
            stationsInPartition[representative(s1.id())] = s2.id();
            return this;
        }

        /**
         * Builds the instance of StationPartition with the connected stations
         * @return a new instance of the class StationPartition
         */
        public StationPartition build(){
            //converts the "deep array" into the "plain" one
            this.stationsInPartition = Arrays
                    .stream(this.stationsInPartition)
                    .map(this::representative)
                    .toArray();
            return new StationPartition(stationsInPartition);
        }
    }
}
