package dev.varion.hermes;

import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.NatsMessageBroker;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import dev.varion.hermes.packet.serdes.jackson.JacksonPacketSerdes;
import io.nats.client.Nats;
import java.io.IOException;

public final class PingClient {

  private PingClient() {}

  public static void main(final String[] args) throws IOException, InterruptedException {
    final PacketSerdes packetSerdes = JacksonPacketSerdes.create();
    final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(NatsMessageBroker.create(Nats.connect(), packetSerdes))
            .withPacketSerdes(packetSerdes)
            .withLoggerFacade(LoggerFacade.create(true))
            .build();

    hermes
        .<PongPacket, PingPacket>request("tests", new PingPacket("Ping!"))
        .thenAccept(
            packet ->
                System.out.printf(
                    "Received: %s -> %s%n", packet.getUniqueId(), packet.getMessage()))
        .join();

    hermes.publish("tests", new PeerPacket("Hello from client!"));
  }
}
