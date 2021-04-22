package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Serde<T> {
    /**
     * Methods that serialize an element of type T
     * @param toSerialize element that you want to serialize
     * @return the serialized element
     */
    abstract public String serialize(T toSerialize);

    /**
     * Methods that deserialize a String into its original object of type T
     * @param toDeserialize element that you want to serialize
     * @return the corresponding element that was originally serialized
     */
    abstract public T deserialize(String toDeserialize);

    public static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer){
        return new Serde<T>() {
            @Override
            public String serialize(T toSerialize) {
                return serializer.apply(toSerialize);
            }

            @Override
            public T deserialize(String toDeserialize) {
                return deserializer.apply(toDeserialize);
            }
        };
    }
    public static <T> Serde<T> oneOf(List<T> enumList){
        return new Serde<T>() {
            @Override
            public String serialize(T toSerialize) {
                return Integer.toString(enumList.indexOf(toSerialize));
            }

            @Override
            public T deserialize(String toDeserialize) {
                return enumList.get(Integer.parseInt(toDeserialize));
            }
        };
    }
    public static <T> Serde<List<T>> listOf(Serde<T> serde, char separator){
        return new Serde<List<T>>() {
            @Override
            public String serialize(List<T> toSerialize) {
                return toSerialize.stream().map(serde::serialize).collect(Collectors.joining(String.valueOf(separator)));
            }

            @Override
            public List<T> deserialize(String toDeserialize) {
                return Arrays.stream(toDeserialize.split(String.valueOf(separator)))
                        .map(serde::deserialize).collect(Collectors.toList());
            }
        };
    }
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char separator){
        return new Serde<SortedBag<T>>() {
            @Override
            public String serialize(SortedBag<T> toSerialize) {
                return toSerialize.stream().map(serde::serialize).collect(Collectors.joining(String.valueOf(separator)));
            }

            @Override
            public SortedBag<T> deserialize(String toDeserialize) {
                return SortedBag.of(Arrays.stream(toDeserialize.split(String.valueOf(separator)))
                        .map(serde::deserialize).collect(Collectors.toList()));
            }
        };
    }
}
