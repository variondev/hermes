package dev.varion.hermes.kv;

public interface KeyValueStorage {
  boolean put(String key, String value) throws KeyValueException;

  String get(String key) throws KeyValueException;

  boolean delete(String key) throws KeyValueException;
}
