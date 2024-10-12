package dev.varion.hermes.packet.callback.requester;

import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface PacketCallbackRequester {

  static PacketCallbackRequester create(
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    return new PacketCallbackRequesterImpl(
        messageBroker, packetSerdes, packetCallbackFacade, requestCleanupInterval);
  }

  <T extends Packet> CompletableFuture<T> request(String channelName, Packet request);
}
