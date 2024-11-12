package dev.varion.hermes.pubsub;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface PacketPublisher {

  static PacketPublisher create(final PacketBroker packetBroker, final PacketCodec packetCodec) {
    return new PacketPublisherImpl(packetBroker, packetCodec);
  }

  <T extends Packet> CompletionStage<Long> publish(String channelName, T packet)
      throws EventPublishingException;
}
