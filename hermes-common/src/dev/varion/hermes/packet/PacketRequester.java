package dev.varion.hermes.packet;

import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallback;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface PacketRequester {

  static PacketRequester create(
      final MessageBroker messageBroker,
      final PacketSerdes packetSerdes,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    return new PacketRequesterImpl(
        messageBroker, packetSerdes, packetCallbackFacade, requestCleanupInterval);
  }

  <T extends Packet & PacketCallback, R extends Packet & PacketCallback>
      CompletableFuture<T> request(String channelName, R packet);
}
