package dev.varion.hermes.keyvalue;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.nats.client.KeyValue;
import io.nats.client.api.KeyValueEntry;

public final class NatsKeyValueStorage implements KeyValueStorage {
  private final KeyValue kvStore;

  NatsKeyValueStorage(final KeyValue kvStore) {
    this.kvStore = kvStore;
  }

  public static KeyValueStorage create(final KeyValue kvStore) {
    return new NatsKeyValueStorage(kvStore);
  }

  @Override
  public boolean set(final String key, final String value) throws KeyValueException {
    return performSafely(
        () -> kvStore.put(key, value.getBytes(UTF_8)) > 0, "Failed to put value in NATS KV store");
  }

  @Override
  public String retrieve(final String key) throws KeyValueException {
    return performSafely(
        () -> {
          final KeyValueEntry entry = kvStore.get(key);
          if (entry != null) {
            return new String(entry.getValue(), UTF_8);
          }
          return null;
        },
        "Failed to delete key from NATS KV store");
  }

  @Override
  public boolean remove(final String key) throws KeyValueException {
    return performSafely(
        () -> {
          kvStore.delete(key);
          return true;
        },
        "Failed to delete key from NATS KV store");
  }
}
