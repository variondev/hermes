package dev.varion.hermes.bridge.nats.jnats.keyvalue;

import static java.nio.charset.StandardCharsets.UTF_8;

import dev.varion.hermes.keyvalue.KeyValueException;
import dev.varion.hermes.keyvalue.KeyValueStorage;
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
        try {
            kvStore.put(key, value.getBytes(UTF_8));
            return true;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to put value in NATS KV store", exception);
        }
    }

    @Override
    public String retrieve(final String key) throws KeyValueException {
        try {
            final KeyValueEntry entry = kvStore.get(key);
            if (entry != null) {
                return new String(entry.getValue(), UTF_8);
            }
            return null;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to get value from NATS KV store", exception);
        }
    }

    @Override
    public boolean remove(final String key) throws KeyValueException {
        try {
            kvStore.delete(key);
            return true;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to delete key from NATS KV store", exception);
        }
    }
}
