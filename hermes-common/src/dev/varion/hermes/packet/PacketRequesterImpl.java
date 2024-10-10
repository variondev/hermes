package dev.varion.hermes.packet;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.WARNING;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.MessageProcessingException;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.util.concurrent.CompletableFuture;

final class PacketRequesterImpl implements PacketRequester {

  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;

  PacketRequesterImpl(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes) {
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
  }

  @Override
  public <T extends Packet, R extends Packet> CompletableFuture<R> request(
      final String channelName, final T packet) {
    loggerFacade.log(
        FINEST,
        "Requesting packet of type %s (%s) over the channel %s",
        packet.getClass().getSimpleName(),
        packet.getUniqueId(),
        channelName);

    try {
      final byte[] payload = packetSerdes.serialize(packet);
      loggerFacade.log(
          FINEST,
          "Request of packet of type %s (%s) has been completed over the %s channel with payload of %d bytes",
          packet.getClass().getSimpleName(),
          packet.getUniqueId(),
          channelName,
          payload.length);
      return messageBroker
          .request(channelName, payload)
          .<R>thenCompose(this::processIncomingPacket)
          .exceptionally(
              cause -> {
                loggerFacade.log(
                    WARNING,
                    "Request of packet of type %s (%s) failed over the channel %s. Error: %s",
                    packet.getClass().getSimpleName(),
                    packet.getUniqueId(),
                    channelName,
                    cause.getMessage());
                return null;
              });
    } catch (final Exception exception) {
      throw new EventPublishingException(
          "Could not request packet over the message broker, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public <T extends Packet> CompletableFuture<T> processIncomingPacket(final byte[] message)
      throws MessageProcessingException {
    return supplyAsync(
            () -> {
              try {
                //noinspection unchecked
                final T receivedPacket = (T) packetSerdes.deserialize(message);
                loggerFacade.log(
                    FINEST,
                    "Request of packet of type %s (%s) has been fulfilled over the temporary channel with payload of %d bytes.",
                    receivedPacket.getClass().getSimpleName(),
                    receivedPacket.getUniqueId(),
                    message.length);
                return receivedPacket;
              } catch (final Exception exception) {
                throw new MessageProcessingException(
                    "Could not process incoming request packet, because of unexpected exception. (%s)"
                        .formatted(exception.getMessage()),
                    exception);
              }
            })
        .exceptionally(
            exception -> {
              throw new MessageProcessingException(exception);
            });
  }
}
