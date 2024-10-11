package dev.varion.hermes.packet;

import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.callback.PacketCallbackRequest;
import dev.varion.hermes.packet.callback.PacketCallbackResponse;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface PacketRequester {

  static PacketRequester create(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    return new PacketRequesterImpl(
        loggerFacade, messageBroker, packetSerdes, packetCallbackFacade, requestCleanupInterval);
  }

  <T extends Packet & PacketCallbackResponse, R extends Packet & PacketCallbackRequest>
      CompletableFuture<T> request(String channelName, R packet);
}
