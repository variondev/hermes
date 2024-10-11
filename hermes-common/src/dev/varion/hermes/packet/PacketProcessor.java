package dev.varion.hermes.packet;

import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;

public interface PacketProcessor {

  static PacketProcessor create(final LoggerFacade loggerFacade, final PacketSerdes packetSerdes) {
    return new PacketProcessorImpl(loggerFacade, packetSerdes);
  }

  <T extends Packet> T processIncomingPacket(final byte[] message) throws PacketProcessingException;
}
