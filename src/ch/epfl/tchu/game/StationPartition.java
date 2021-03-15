package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {
    int[] stationsInPartition;

    //stationsInPartition = new int[];
    private StationPartition(int[] stationsInPartition){
        this.stationsInPartition = stationsInPartition;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return stationsInPartition[s1.id()] == stationsInPartition[s2.id()];
        //TODO check other cases
        //return s1.id() == s2.id();
    }

    public final static class Builder{
        private int[] stationsInPartition;
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            this.stationsInPartition =  IntStream.range(0, stationCount).toArray();
            // TODO check IntStream
            // this.stationsInPartition = new int[stationCount];
            //for (int i = 0; i < stationCount; ++i){
            //    this.stationsInPartition[i] = i;
            //}
        }

        private int representative(int id){
            for (int i = 0; i < stationsInPartition.length; ++i){
                if (this.stationsInPartition[i] == id){
                    return id;
                }
            }
            return representative(id);
        }

        public Builder connect(Station s1, Station s2){
            stationsInPartition[s1.id()] = representative(s2.id());
            //TODO
            return this;
        }

        public StationPartition build(){
            //converts the "deep array" into the plain one
            // check map
            this.stationsInPartition =
                    Arrays.stream(this.stationsInPartition)
                    .map((id) -> representative(id))
                    .toArray();
            // TODO check map
            // for (int i = 0; i < this.stationsInPartition.length; ++i){
            //    this.stationsInPartition[i] = representative(this.stationsInPartition[i]);
            //}
            return new StationPartition(stationsInPartition);
        }
    }
}
