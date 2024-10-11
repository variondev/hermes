package dev.varion.hermes.packet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.FINEST;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.callback.PacketCallbackRequest;
import dev.varion.hermes.packet.callback.PacketCallbackResponse;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class PacketRequesterImpl implements PacketRequester {

  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;
  private final PacketCallbackFacade packetCallbackFacade;
  private final Duration requestCleanupInterval;

  PacketRequesterImpl(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
    this.packetCallbackFacade = packetCallbackFacade;
    this.requestCleanupInterval = requestCleanupInterval;
  }

  @Override
  public <T extends Packet & PacketCallbackResponse, R extends Packet & PacketCallbackRequest>
      CompletableFuture<T> request(final String channelName, final R packet) {
    final UUID uniqueId = packet.getUniqueId();
    loggerFacade.log(
        FINEST,
        "Requesting packet of type %s (%s) over the channel %s",
        packet.getClass().getSimpleName(),
        uniqueId,
        channelName);

    try {
      loggerFacade.log(
          FINEST,
          "Request of packet of type %s (%s) has been sent over the %s channel",
          packet.getClass().getSimpleName(),
          uniqueId,
          channelName);

      final CompletableFuture<T> completableFuture = new CompletableFuture<>();
      messageBroker.publish(channelName, packetSerdes.serialize(packet));
      packetCallbackFacade.add(uniqueId, completableFuture);
      return completableFuture.orTimeout(requestCleanupInterval.toMillis(), MILLISECONDS);

    } catch (final Exception exception) {
      throw new EventPublishingException(
          "Could not request packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
