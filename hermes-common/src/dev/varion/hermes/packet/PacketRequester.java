package dev.varion.hermes.packet;

import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.MessageProcessingException;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.util.concurrent.CompletableFuture;

public interface PacketRequester {

  static PacketRequester create(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes) {
    return new PacketRequesterImpl(loggerFacade, messageBroker, packetSerdes);
  }

  <T extends Packet, R extends Packet> CompletableFuture<R> request(String channelName, T packet);

  <T extends Packet> CompletableFuture<T> processIncomingPacket(byte[] message)
      throws MessageProcessingException;
}
