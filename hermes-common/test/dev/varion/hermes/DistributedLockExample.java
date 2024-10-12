package dev.varion.hermes;

import dev.varion.hermes.bridge.redis.lettuce.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.bridge.redis.lettuce.message.RedisMessageBroker;
import dev.varion.hermes.distributed.DistributedLock;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;
import java.util.concurrent.TimeUnit;

public final class DistributedLockExample {

  private DistributedLockExample() {}

  public static void main(final String[] args) {
    final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(RedisMessageBroker.create(redisClient))
            .withPacketSerdes(MessagePackCodec.create())
            .withKeyValueStorage(RedisKeyValueStorage.create(redisClient))
            .withDistributedLocks(true)
            .build()) {

      final DistributedLock lock = hermes.locks().createLock("my_resource");

      if (lock.lock(TimeUnit.SECONDS.toMillis(5L))) {
        System.out.println("Lock acquired!");

        lock.unlock();
      } else {
        System.out.println("Failed to acquire lock.");
      }
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }
}
