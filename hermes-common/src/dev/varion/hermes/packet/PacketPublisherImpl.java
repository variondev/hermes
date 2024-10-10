package dev.varion.hermes.packet;

import static java.util.logging.Level.FINEST;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.serdes.PacketSerdes;

final class PacketPublisherImpl implements PacketPublisher {

  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;

  PacketPublisherImpl(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes) {
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
  }

  @Override
  public <T extends Packet> void publish(final String channelName, final T packet) {
    loggerFacade.log(
        FINEST,
        "Publishing packet of type %s (%s) over the channel %s",
        packet.getClass().getSimpleName(),
        packet.getUniqueId(),
        channelName);

    try {
      final byte[] payload = packetSerdes.serialize(packet);
      messageBroker.publish(channelName, payload);
      loggerFacade.log(
          FINEST,
          "Packet of type %s (%s) has been published over the %s channel with payload of %d bytes",
          packet.getClass().getSimpleName(),
          packet.getUniqueId(),
          channelName,
          payload.length);
    } catch (final Exception exception) {
      throw new EventPublishingException(
          "Could not publish packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
