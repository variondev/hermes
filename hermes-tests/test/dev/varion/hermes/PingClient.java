package dev.varion.hermes;

import dev.varion.hermes.message.RedisMessageBroker;
import dev.varion.hermes.packet.serdes.jackson.JacksonPacketSerdes;
import io.lettuce.core.RedisClient;
import java.io.IOException;

public final class PingClient {

  private PingClient() {}

  public static void main(final String[] args) throws IOException, InterruptedException {
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(RedisMessageBroker.create(RedisClient.create()))
            .withPacketSerdes(JacksonPacketSerdes.create())
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
