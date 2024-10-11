package dev.varion.hermes.packet.callback;

import static java.util.logging.Level.FINEST;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PacketCallbackSubscriber implements HermesListener {

  private final LoggerFacade loggerFacade;
  private final PacketSerdes packetSerdes;
  private final PacketCallbackFacade packetCallbackFacade;

  PacketCallbackSubscriber(
      final LoggerFacade loggerFacade,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade) {
    this.loggerFacade = loggerFacade;
    this.packetSerdes = packetSerdes;
    this.packetCallbackFacade = packetCallbackFacade;
  }

  public static PacketCallbackSubscriber create(
      final LoggerFacade loggerFacade,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade) {
    return new PacketCallbackSubscriber(loggerFacade, packetSerdes, packetCallbackFacade);
  }

  @Override
  public void receive(final String channelName, final byte[] payload) {
    final Packet packet = packetSerdes.deserialize(payload);
    final UUID uniqueId = packet.getUniqueId();
    packetCallbackFacade
        .findByUniqueId(uniqueId)
        .ifPresent(
            future -> {
              if (packet instanceof final PacketCallbackResponse response) {
                //noinspection unchecked
                ((CompletableFuture<PacketCallbackResponse>) future).complete(response);
                loggerFacade.log(
                    FINEST,
                    "Request of %s has been completed with response %s (%s)",
                    uniqueId,
                    response.getClass().getSimpleName(),
                    response.getUniqueId());
                packetCallbackFacade.remove(uniqueId);
              }
            });
  }
}
