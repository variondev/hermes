package dev.varion.hermes.keyvalue;

public interface KeyValueStorage {
    boolean set(String key, String value) throws KeyValueException;

    String retrieve(String key) throws KeyValueException;

    boolean remove(String key) throws KeyValueException;
}
