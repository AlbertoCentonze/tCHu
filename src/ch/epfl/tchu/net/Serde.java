package ch.epfl.tchu.net;

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
}
