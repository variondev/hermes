package dev.varion.hermes.packet.pubsub;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.serdes.PacketSerdes;

@FunctionalInterface
public interface PacketSubscriber {

  static PacketSubscriber create(
      final EventBus eventBus,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketCallbackFacade packetCallbackFacade,
      final PacketSerdes packetSerdes) {
    return new PacketSubscriberImpl(
        eventBus, messageBroker, packetPublisher, packetCallbackFacade, packetSerdes);
  }

  void subscribe(Subscriber subscriber) throws PacketSubscribingException;
}
