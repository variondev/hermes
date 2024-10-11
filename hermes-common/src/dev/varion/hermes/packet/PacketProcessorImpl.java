package dev.varion.hermes.packet;

import static java.util.logging.Level.FINEST;

import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;

final class PacketProcessorImpl implements PacketProcessor {

  private final LoggerFacade loggerFacade;
  private final PacketSerdes packetSerdes;

  PacketProcessorImpl(final LoggerFacade loggerFacade, final PacketSerdes packetSerdes) {
    this.loggerFacade = loggerFacade;
    this.packetSerdes = packetSerdes;
  }

  @Override
  public <T extends Packet> T processIncomingPacket(final byte[] message)
      throws PacketProcessingException {
    try {
      //noinspection unchecked
      final T receivedPacket = (T) packetSerdes.deserialize(message);
      loggerFacade.log(
          FINEST,
          "Packet of type %s (%s) has been processed with payload of %d bytes.",
          receivedPacket.getClass().getSimpleName(),
          receivedPacket.getUniqueId(),
          message.length);
      return receivedPacket;
    } catch (final Exception exception) {
      throw new PacketProcessingException(
          "Could not process incoming request packet, because of unexpected exception. (%s)"
              .formatted(exception.getMessage()),
          exception);
    }
  }
}
