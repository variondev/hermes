package dev.varion.hermes.packet;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.serdes.PacketSerdes;

@FunctionalInterface
public interface PacketPublisher {

  static PacketPublisher create(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes) {
    return new PacketPublisherImpl(loggerFacade, messageBroker, packetSerdes);
  }

  <T extends Packet> void publish(String channelName, T packet) throws EventPublishingException;
}
