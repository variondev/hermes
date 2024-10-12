package dev.varion.hermes;

import dev.varion.hermes.bridge.redis.lettuce.message.RedisMessageBroker;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;

public final class PingClient {

  private PingClient() {}

  public static void main(final String[] args) {
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(
                RedisMessageBroker.create(RedisClient.create("redis://localhost:6379")))
            .withPacketSerdes(MessagePackCodec.create())
            .build()) {
      hermes
          .<PongMessage>request("tests", new PingMessage("Ping!"))
          .thenAccept(
              packet ->
                  System.out.printf(
                      "Received: %s -> %s%n", packet.getUniqueId(), packet.getContent()))
          .join();

      hermes.publish("tests", new PeerMessage("Hello from client!"));
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }
}
