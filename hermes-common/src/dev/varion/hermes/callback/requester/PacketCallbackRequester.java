package dev.varion.hermes.callback.requester;

import dev.varion.hermes.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface PacketCallbackRequester {

  static PacketCallbackRequester create(
      final PacketBroker packetBroker,
      final PacketCodec packetCodec,
      final PacketCallbackFacade packetCallbackFacade,
      final Duration requestCleanupInterval) {
    return new PacketCallbackRequesterImpl(
        packetBroker, packetCodec, packetCallbackFacade, requestCleanupInterval);
  }

  <T extends Packet> CompletableFuture<T> request(String channelName, Packet request);
}
