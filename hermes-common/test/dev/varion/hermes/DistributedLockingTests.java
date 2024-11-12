package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.JacksonPacketCodecFactory.getJacksonPacketCodec;
import static dev.varion.hermes.packet.codec.MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper;
import static java.lang.System.exit;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import dev.varion.hermes.distributed.DistributedLock;
import dev.varion.hermes.keyvalue.NatsKeyValueStorage;
import dev.varion.hermes.packet.NatsPacketBroker;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.api.KeyValueConfiguration;
import io.nats.client.api.StorageType;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public final class DistributedLockingTests {

  private DistributedLockingTests() {}

  public static void main(final String[] args) throws Exception {
    final Connection connection = Nats.connect();

    try {
      final KeyValueConfiguration kvConfig =
          KeyValueConfiguration.builder().name("my_locks").storageType(StorageType.Memory).build();
      connection.keyValueManagement().create(kvConfig);
    } catch (final Exception ignored) {
    }

    final Hermes hermes =
        HermesConfigurator.configure(
            configurator ->
                configurator
                    .messageBroker(config -> config.using(NatsPacketBroker.create(connection)))
                    .messageCodec(
                        config ->
                            config.using(getJacksonPacketCodec(getMsgpackJacksonObjectMapper())))
                    .keyValue(
                        config -> {
                          try {
                            config.using(
                                NatsKeyValueStorage.create(connection.keyValue("my_locks")));
                          } catch (final IOException e) {
                            throw new RuntimeException(e);
                          }
                        })
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
                                  ofSeconds(5L))
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
