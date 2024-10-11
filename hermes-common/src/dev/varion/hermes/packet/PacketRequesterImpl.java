package dev.varion.hermes.packet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallback;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class PacketRequesterImpl implements PacketRequester {

  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;
  private final PacketCallbackFacade packetCallbackFacade;
  private final Duration requestCleanupInterval;

  PacketRequesterImpl(
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
    this.packetCallbackFacade = packetCallbackFacade;
    this.requestCleanupInterval = requestCleanupInterval;
  }

  @Override
  public <T extends Packet & PacketCallback, R extends Packet & PacketCallback>
      CompletableFuture<T> request(final String channelName, final R packet) {
    final UUID uniqueId = packet.getUniqueId();
    try {
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
