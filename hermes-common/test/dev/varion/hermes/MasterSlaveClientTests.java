package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper;

import dev.varion.hermes.packet.RedisPacketBroker;
import dev.varion.hermes.packet.codec.JacksonPacketCodecFactory;
import io.lettuce.core.RedisClient;

public final class MasterSlaveClientTests {

  private MasterSlaveClientTests() {}

  public static void main(final String[] args) {
    try (final Hermes hermes =
        HermesConfigurator.configure(
            configurator ->
                configurator
                    .packetBroker(
                        config ->
                            config.using(
                                RedisPacketBroker.create(
                                    RedisClient.create("redis://localhost:6379"))))
                    .packetCodec(
                        config ->
                            config.using(
                                JacksonPacketCodecFactory.getJacksonPacketCodec(
                                    getMsgpackJacksonObjectMapper()))))) {
      hermes
          .<MasterSlaveResponsePacket>request("tests", new MasterSlaveRequestPacket("Ping!"))
          .thenAccept(
              packet ->
                  System.out.printf(
                      "Received: %s -> %s%n", packet.getUniqueId(), packet.getContent()))
          .join();

      hermes.publish("tests", new BroadcastPacket("Hello from client!"));
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }
}
