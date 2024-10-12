package dev.varion.hermes;

import static java.lang.System.exit;
import static java.time.Duration.ofSeconds;

import dev.varion.hermes.bridge.redis.lettuce.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.bridge.redis.lettuce.message.RedisMessageBroker;
import dev.varion.hermes.distributed.DistributedLock;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;
import java.io.IOException;

public final class DistributedLocking {

  private DistributedLocking() {}

  public static void main(final String[] args) {
    final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
    final Hermes hermes =
        HermesConfigurator.configure(
            configurator ->
                configurator
                    .messageBroker(config -> config.using(RedisMessageBroker.create(redisClient)))
                    .messageCodec(config -> config.using(MessagePackCodec.create()))
                    .keyValue(config -> config.using(RedisKeyValueStorage.create(redisClient)))
                    .distributedLock(config -> config.using(true)));
    final DistributedLock lock = hermes.distributedLocks().createLock("my_resource");
    lock.execute(() -> System.out.println("Lock acquired!"), ofSeconds(1L), ofSeconds(5L))
        .whenComplete(
            (v, t) -> {
              System.out.println("Lock released!");
              try {
                hermes.close();
              } catch (final IOException exception) {
                throw new RuntimeException(exception);
              } finally {
                exit(0);
              }
            })
        .join();
  }
}
