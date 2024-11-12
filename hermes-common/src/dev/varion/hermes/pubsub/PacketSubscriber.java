package dev.varion.hermes.pubsub;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;

@FunctionalInterface
public interface PacketSubscriber {

  static PacketSubscriber create(
      final EventBus eventBus,
      final PacketBroker packetBroker,
      final PacketPublisher packetPublisher,
      final PacketCallbackFacade packetCallbackFacade,
      final PacketCodec packetCodec) {
    return new PacketSubscriberImpl(
        eventBus, packetBroker, packetPublisher, packetCallbackFacade, packetCodec);
  }

  void subscribe(Subscriber subscriber) throws PacketSubscribingException;
}
