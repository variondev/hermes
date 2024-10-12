package dev.varion.hermes.packet.pubsub;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.PacketSerdes;

@FunctionalInterface
public interface PacketPublisher {

  static PacketPublisher create(
      final MessageBroker messageBroker, final PacketSerdes packetSerdes) {
    return new PacketPublisherImpl(messageBroker, packetSerdes);
  }

  <T extends Packet> void publish(String channelName, T packet) throws EventPublishingException;
}
