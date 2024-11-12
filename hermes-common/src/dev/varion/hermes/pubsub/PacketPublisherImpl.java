package dev.varion.hermes.pubsub;

import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.util.concurrent.CompletionStage;

final class PacketPublisherImpl implements PacketPublisher {

  private final PacketBroker packetBroker;
  private final PacketCodec packetCodec;

  PacketPublisherImpl(final PacketBroker packetBroker, final PacketCodec packetCodec) {
    this.packetBroker = packetBroker;
    this.packetCodec = packetCodec;
  }

  @Override
  public <T extends Packet> CompletionStage<Long> publish(
      final String channelName, final T packet) {
    try {
      final byte[] payload = packetCodec.serialize(packet);
      return packetBroker.publish(channelName, payload);
    } catch (final Exception exception) {
      throw new PacketPublishingException(
          "Could not publish packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
