package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.JacksonPacketCodecFactory.getJacksonPacketCodec;
import static dev.varion.hermes.packet.codec.MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper;
import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.keyvalue.RedisKeyValueStorage;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.RedisPacketBroker;
import io.lettuce.core.RedisClient;
import java.time.Duration;

public final class MasterSlaveServerTests {

  private MasterSlaveServerTests() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {

    try (final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        final Hermes hermes =
            HermesConfigurator.configure(
                configurator ->
                    configurator
                        .packetBroker(
                            config -> config.using(RedisPacketBroker.create(redisClient)))
                        .keyValue(config -> config.using(RedisKeyValueStorage.create(redisClient)))
                        .distributedLock(config -> config.using(true))
                        .packetCallback(
                            config -> config.requestCleanupInterval(Duration.ofSeconds(10L)))
                        .packetCodec(
                            config ->
                                config.using(
                                    getJacksonPacketCodec(getMsgpackJacksonObjectMapper()))))) {

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
    public MasterSlaveResponsePacket receive(final MasterSlaveRequestPacket request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      final MasterSlaveResponsePacket response =
          new MasterSlaveResponsePacket(request.getContent() + " Pong!");
      return response.dispatchTo(request.getUniqueId());
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
