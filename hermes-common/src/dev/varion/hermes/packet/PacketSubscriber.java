package dev.varion.hermes.packet;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.MessageProcessingException;
import dev.varion.hermes.packet.serdes.PacketSerdes;

public interface PacketSubscriber {

  static PacketSubscriber create(
      final EventBus eventBus,
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketSerdes packetSerdes) {
    return new PacketSubscriberImpl(
        eventBus, loggerFacade, messageBroker, packetPublisher, packetSerdes);
  }

  void subscribe(Subscriber subscriber);

  <T extends Packet> T processIncomingPacket(byte[] payload) throws MessageProcessingException;
}
