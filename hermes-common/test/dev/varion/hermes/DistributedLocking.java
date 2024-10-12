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

    // Create a fixed thread pool
    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Create 10 threads that will continuously try to acquire and release the lock
    IntStream.range(0, 10)
        .forEach(
            i ->
                executorService.submit(
                    () -> {
                      while (true) {
                        try {
                          lock.execute(
                                  () -> {
                                    // Lock acquired, perform some operation
                                    System.out.println("Thread " + i + " acquired the lock!");
                                    // Simulate some work with a sleep
                                    try {
                                      Thread.sleep(100);
                                    } catch (final InterruptedException ignored) {
                                    }
                                  },
                                  ofMillis(100L),
                                  ofSeconds(5L))
                              .whenComplete(
                                  (v, t) ->
                                      System.out.println("Thread " + i + " released the lock!"))
                              .join();
                        } catch (final Exception exception) {
                          exception.printStackTrace();
                        }
                      }
                    }));

    // Shutdown the executor service, will let tasks finish (never terminates in this example)
    executorService.shutdown();

    // Wait for all tasks to finish
    while (!executorService.isTerminated()) {}

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
