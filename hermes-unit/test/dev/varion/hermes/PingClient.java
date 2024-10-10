package dev.varion.hermes;

import static dev.varion.hermes.logger.LoggerFacade.getLoggerFacade;

import dev.varion.hermes.message.NatsMessageBroker;
import dev.varion.hermes.serdes.jackson.JacksonPacketSerdes;
import io.nats.client.Nats;
import java.io.IOException;

public final class PingClient {

  private PingClient() {}

  public static void main(final String[] args) throws IOException, InterruptedException {
    final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(NatsMessageBroker.create(Nats.connect()))
            .withPacketSerdes(JacksonPacketSerdes.create())
            .withLoggerFacade(getLoggerFacade(true))
            .build();

    hermes
        .<PingPacket, PongPacket>request("tests", new PingPacket("Ping!"))
        .thenAccept(
            packet ->
                System.out.println(
                    "Received: " + packet.getUniqueId() + " - " + packet.getMessage()))
        .join();
  }
}
