package dev.varion.hermes;

import dev.varion.hermes.message.RedisMessageBroker;
import dev.varion.hermes.packet.serdes.MessagePackSerdes;
import io.lettuce.core.RedisClient;

public final class PingClient {

  private PingClient() {}

  public static void main(final String[] args) {
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(
                RedisMessageBroker.create(RedisClient.create("redis://localhost:6379")))
            .withPacketSerdes(MessagePackSerdes.create())
            .build()) {
      hermes
          .<PongPacket>request("tests", new PingPacket("Ping!"))
          .thenAccept(
              packet ->
                  System.out.printf(
                      "Received: %s -> %s%n", packet.getUniqueId(), packet.getMessage()))
          .join();

      hermes.publish("tests", new PeerPacket("Hello from client!"));
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }
}
