package dev.varion.hermes.packet.pubsub;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.PacketSerdes;

final class PacketPublisherImpl implements PacketPublisher {

  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;

  PacketPublisherImpl(final MessageBroker messageBroker, final PacketSerdes packetSerdes) {
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
  }

  @Override
  public <T extends Packet> void publish(final String channelName, final T packet) {
    try {
      final byte[] payload = packetSerdes.serialize(packet);
      messageBroker.publish(channelName, payload);
    } catch (final Exception exception) {
      throw new EventPublishingException(
          "Could not publish packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
