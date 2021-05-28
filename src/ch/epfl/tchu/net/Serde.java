package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alberto Centonze (327267)
 *
 * An interface to serialize and deserialize game infos
 * @param <T> the type to serialize
 */
public interface Serde<T> {
    /**
     * Methods that serialize an instance of a class
     * @param toSerialize element that you want to serialize
     * @return the serialized element
     */
    String serialize(T toSerialize);

    /**
     * Methods that deserialize a String into its original object
     * @param toDeserialize element that you want to serialize
     * @return the corresponding element that was originally serialized
     */
    T deserialize(String toDeserialize);

    /**
     * Creates a Serde able to operate with the specified class
     * @param serializer the function that will serialize an instance of the class
     * @param deserializer the function that will deserialize an instance of the class
     * @param <T> the class of the instance that has to be serialized
     * @return an instance of serde specific for that class
     */
    static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<>() {
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

    /**
     * Creates a Serde able to operate with enums of a specified class.
     * It creates a one-to-one correspondence between the index of the list and
     * the element of the enum
     * @param enumList a list with all the values of the enum
     * @param <T> The enum to serialize
     * @return an instance of serde able to serialize and deserialize any element according to the specified order
     */
    static <T> Serde<T> oneOf(List<T> enumList) {
        return new Serde<>() {
            @Override
            public String serialize(T toSerialize) {
                return Integer.toString(enumList.indexOf(toSerialize));
            }

            @Override
            public T deserialize(String toDeserialize) {
                return toDeserialize.isEmpty() ? null : enumList.get(Integer.parseInt(toDeserialize));
            }
        };
    }

    /**
     * Creates a Serde able to operate with lists of a specified class.
     * All the elements are encoded using a given Serde and a separator character
     * @param serde the Serde used to encode the elements of the class
     * @param separator the char used to separate the encoded elements
     * @param <T> the type of the list
     * @return an instance of Serde able to serialize and deserialize lists of the specified class
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, char separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> toSerialize) {
                return toSerialize.stream().map(serde::serialize).collect(Collectors.joining(String.valueOf(separator)));
            }

            @Override
            public List<T> deserialize(String toDeserialize) {
                return toDeserialize.isEmpty() ? List.of() :
                        Arrays.stream(toDeserialize.split(String.valueOf(separator)))
                        .map(serde::deserialize).collect(Collectors.toList());
            }
        };
    }

    /**
     * Creates a Serde able to operate with SortedBags of a specified class
     * All the elements are encoded using a given Serde and a separator character
     * @param serde the Serde used to encode the elements of the class
     * @param separator the char used to separate the encoded elements
     * @param <T> the type of the list
     * @return an instance of Serde able to serialize and deserialize SortedBag of the specified class
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char separator) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> toSerialize) {
                return toSerialize.stream().map(serde::serialize).collect(Collectors.joining(String.valueOf(separator)));
            }

            @Override
            public SortedBag<T> deserialize(String toDeserialize) {
                return toDeserialize.isEmpty() ? SortedBag.of() :
                        SortedBag.of(Arrays.stream(toDeserialize.split(String.valueOf(separator)))
                        .map(serde::deserialize).collect(Collectors.toList()));
            }
        };
    }
}
