package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.JacksonPacketCodecFactory.getJacksonPacketCodec;
import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.completedFuture;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.RedisPacketBroker;
import io.lettuce.core.RedisClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class MasterSlaveServerTests {

  private MasterSlaveServerTests() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {
    try (final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        final Hermes hermes =
            HermesConfigurator.configure(
                configurator ->
                    configurator
                        .packetBroker(config -> config.using(RedisPacketBroker.create(redisClient)))
                        .keyValue(config -> config.using(RedisKeyValueStorage.create(redisClient)))
                        .distributedLock(config -> config.using(true))
                        .packetCallback(
                            config -> config.requestCleanupInterval(Duration.ofSeconds(10L)))
                        .packetCodec(config -> config.using(getJacksonPacketCodec())))) {
      System.out.println("starting");
      hermes.subscribe(new PongListener());

      // keep-alive
      while (true) {
        sleep(1000);
      }
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }

  public static final class PongListener implements Subscriber {

    @Subscribe
    public CompletableFuture<MasterSlaveResponsePacket> receive(final MasterSlaveRequestPacket request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      //      final Map<UUID, ExampleEnum> queuePlayers,
      //      final int port,
      //      final int memoryUsagePercent,
      //      final double tps,
      //      final String address,
      //      final ArrayList<ExampleEnum> serverCategory,
      //      final Map<UUID, String> players,
      //      final long bootTime,
      //      final int maxPlayers) {
      final MasterSlaveResponsePacket response =
          new MasterSlaveResponsePacket(
              Map.of(UUID.randomUUID(), MasterSlaveResponsePacket.ExampleEnum.ONE),
              1234,
              30,
              20.0,
              "127.0.0.1",
              new ArrayList<>(
                  Arrays.asList(
                      MasterSlaveResponsePacket.ExampleEnum.TWO,
                      MasterSlaveResponsePacket.ExampleEnum.THREE)),
              Map.of(UUID.randomUUID(), "offplayer"),
              123456789L,
              50);
      // final UUID uniqueId, final String content,
      //      final Map<String, Integer> someMap, final ArrayList<Thread.State> threadStates
      System.out.println("created response");
      return completedFuture(response.dispatchTo(request.getUniqueId()));
    }

    @Subscribe
    public void receive(final BroadcastPacket packet) {
      System.out.printf("Received p2p packet: %s%n", packet.getContent());
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}
