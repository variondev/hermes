package dev.varion.hermes.callback.requester;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import dev.varion.hermes.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class PacketCallbackRequesterImpl implements PacketCallbackRequester {

  private final PacketBroker packetBroker;
  private final PacketCodec packetCodec;
  private final PacketCallbackFacade packetCallbackFacade;
  private final Duration requestCleanupInterval;

  PacketCallbackRequesterImpl(
      final PacketBroker packetBroker,
      final PacketCodec packetCodec,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    this.packetBroker = packetBroker;
    this.packetCodec = packetCodec;
    this.packetCallbackFacade = packetCallbackFacade;
    this.requestCleanupInterval = requestCleanupInterval;
  }

  @Override
  public <T extends Packet> CompletableFuture<T> request(
      final String channelName, final Packet packet) {
    final UUID uniqueId = packet.getUniqueId();
    try {
      final CompletableFuture<T> completableFuture = new CompletableFuture<>();
      packetBroker.publish(channelName, packetCodec.serialize(packet));
      packetCallbackFacade.add(uniqueId, completableFuture);
      return completableFuture.orTimeout(requestCleanupInterval.toMillis(), MILLISECONDS);
    } catch (final Exception exception) {
      throw new PacketRequestingException(
          "Could not request packet over the packet broker, because of unexpected exception.",
          exception);
    }
  }
}
