package dev.varion.hermes.callback;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PacketCallbackSubscriber implements HermesListener {

  private final PacketCodec packetCodec;
  private final PacketCallbackFacade packetCallbackFacade;

  PacketCallbackSubscriber(
      final PacketCodec packetCodec, final PacketCallbackFacade packetCallbackFacade) {
    this.packetCodec = packetCodec;
    this.packetCallbackFacade = packetCallbackFacade;
  }

  public static PacketCallbackSubscriber create(
      final PacketCodec packetCodec, final PacketCallbackFacade packetCallbackFacade) {
    return new PacketCallbackSubscriber(packetCodec, packetCallbackFacade);
  }

  @Override
  public void receive(final String channelName, final byte[] payload) {
    final Packet packet = packetCodec.deserialize(payload);
    if (packet == null) {
      return;
    }

    final UUID uniqueId = packet.getUniqueId();
    packetCallbackFacade
        .findByUniqueId(uniqueId)
        .ifPresent(
            future -> {
              packetCallbackFacade.remove(uniqueId);
              try {
                //noinspection unchecked
                ((CompletableFuture<Packet>) future).complete(packet);
              } catch (final Exception exception) {
                throw new PacketCompletionException(
                    "Failed to complete packet identified by %s".formatted(uniqueId), exception);
              }
            });
  }
}
