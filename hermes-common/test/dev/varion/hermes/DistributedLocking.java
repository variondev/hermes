package dev.varion.hermes;

import static java.lang.System.exit;
import static java.time.Duration.ofMillis;

import dev.varion.hermes.bridge.redis.lettuce.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.bridge.redis.lettuce.message.RedisMessageBroker;
import dev.varion.hermes.distributed.DistributedLock;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // Create a fixed thread pool
    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    for (int j = 0; j < 10; j++) {
      final int i = j;
      executorService.submit(
          () -> {
            try {
              final DistributedLock lock = hermes.distributedLocks().createLock("my_resource");
              lock.execute(
                      () -> {
                        try {
                          Thread.sleep(100);
                        } catch (final InterruptedException e) {
                          e.printStackTrace();
                        }
                        System.out.println("Thread " + i + " acquired the lock!");
                      },
                      ofMillis(1L),
                      ofMillis(1000L))
                  .whenComplete((v, t) -> System.out.println("Thread " + i + " released the lock!"))
                  .join();
            } catch (final Exception exception) {
              exception.printStackTrace();
            }
          });
    }

    executorService.shutdown();
    while (!executorService.isTerminated()) {}

    try {
      hermes.close();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    } finally {
      exit(0);
    }
  }
}
