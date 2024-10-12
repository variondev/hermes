package dev.varion.hermes.bridge.redis.lettuce.keyvalue;

import static java.nio.charset.StandardCharsets.UTF_8;

import dev.varion.hermes.bridge.redis.lettuce.message.codec.RedisBinaryCodec;
import dev.varion.hermes.keyvalue.KeyValueException;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

public final class RedisKeyValueStorage implements KeyValueStorage {

    private final StatefulRedisConnection<String, byte[]> connection;

    RedisKeyValueStorage(final StatefulRedisConnection<String, byte[]> connection) {
        this.connection = connection;
    }

    public static KeyValueStorage create(final StatefulRedisConnection<String, byte[]> connection) {
        return new RedisKeyValueStorage(connection);
    }

    public static KeyValueStorage create(final RedisClient redisClient) {
        return create(redisClient.connect(RedisBinaryCodec.INSTANCE));
    }

    @Override
    public boolean set(final String key, final String value) throws KeyValueException {
        try {
            connection.sync().set(key, value.getBytes(UTF_8));
            return true;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to put value in REDIS KV store", exception);
        }
    }

    @Override
    public String retrieve(final String key) throws KeyValueException {
        try {
            final byte[] entry = connection.sync().get(key);
            if (entry != null) {
                return new String(entry, UTF_8);
            }
            return null;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to get value from REDIS KV store", exception);
        }
    }

    @Override
    public boolean remove(final String key) throws KeyValueException {
        try {
            connection.sync().del(key);
            return true;
        } catch (final Exception exception) {
            throw new KeyValueException("Failed to delete key from NATS KV store", exception);
        }
    }
}
