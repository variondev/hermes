package dev.varion.hermes;

import static java.lang.System.exit;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import dev.varion.hermes.bridge.redis.lettuce.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.bridge.redis.lettuce.message.RedisMessageBroker;
import dev.varion.hermes.distributed.DistributedLock;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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

    final ExecutorService executorService = Executors.newFixedThreadPool(10);
    IntStream.range(0, 10)
        .forEach(
            i ->
                executorService.submit(
                    () -> {
                      while (true) {
                        try {
                          lock.execute(
                                  () -> {
                                    System.out.println("Thread " + i + " acquired the lock!");
                                    try {
                                      Thread.sleep(100);
                                    } catch (final InterruptedException ignored) {
                                    }
                                  },
                                  ofMillis(10L),
                                  ofSeconds(1L))
                              .whenComplete(
                                  (v, t) ->
                                      System.out.println("Thread " + i + " released the lock!"))
                              .join();
                        } catch (final Exception e) {
                          e.printStackTrace();
                        }
                      }
                    }));

    executorService.shutdown();

    while (!executorService.isTerminated()) {
      System.out.println("Waiting for all tasks to finish...");
      try {
        Thread.sleep(1000); // Sleep for a second before checking again
      } catch (final InterruptedException ignored) {
      }
    }

    // Closing hermes resources
    try {
      hermes.close();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    } finally {
      exit(0);
    }
  }
}
