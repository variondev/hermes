package dev.varion.hermes.packet.callback;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PacketCallbackSubscriber implements HermesListener {

  private final PacketSerdes packetSerdes;
  private final PacketCallbackFacade packetCallbackFacade;

  PacketCallbackSubscriber(
      final PacketSerdes packetSerdes, final PacketCallbackFacade packetCallbackFacade) {
    this.packetSerdes = packetSerdes;
    this.packetCallbackFacade = packetCallbackFacade;
  }

  public static PacketCallbackSubscriber create(
      final PacketSerdes packetSerdes, final PacketCallbackFacade packetCallbackFacade) {
    return new PacketCallbackSubscriber(packetSerdes, packetCallbackFacade);
  }

  @Override
  public void receive(final String channelName, final byte[] payload) {
    final Packet packet = packetSerdes.deserialize(payload);
    final UUID uniqueId = packet.getUniqueId();
    packetCallbackFacade
        .findByUniqueId(uniqueId)
        .ifPresent(
            future -> {
              try {
                //noinspection unchecked
                ((CompletableFuture<Packet>) future).complete(packet);
                packetCallbackFacade.remove(uniqueId);
              } catch (final Exception exception) {
                throw new PacketCompleteException(
                    "Failed to complete packet identified by %s".formatted(uniqueId), exception);
              }
            });
  }
}
