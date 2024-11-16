package dev.varion.hermes;

import static dev.varion.hermes.packet.codec.MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                                    ))))) {
      System.out.println("starting");
      hermes
          .<MasterSlaveResponsePacket>request("tests", new MasterSlaveRequestPacket("Ping!"))
          .exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
          })
          .thenAccept(
              packet -> {
                System.out.println("handling response");
                try {
                  System.out.printf("Received: %s", new ObjectMapper().writeValueAsString(packet));
                } catch (final JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              })
          .exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
          })
          .join();

      hermes.publish("tests", new BroadcastPacket("Hello from client!"));
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }
}
