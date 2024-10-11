package dev.varion.hermes.packet;

import static java.util.Arrays.stream;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.SubscribingException;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.callback.PacketCallbackSubscriber;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class PacketSubscriberImpl implements PacketSubscriber {

  private final EventBus eventBus;
  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;

  PacketSubscriberImpl(
      final EventBus eventBus,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketCallbackFacade packetCallbackFacade,
      final PacketSerdes packetSerdes) {
    this.eventBus = eventBus;
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
    eventBus.result(
        Packet.class,
        packet -> {
          packetPublisher.publish("callbacks", packet);
        });
    messageBroker.subscribe(
        "callbacks", PacketCallbackSubscriber.create(packetSerdes, packetCallbackFacade));
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    final String identity = subscriber.identity();
    if (identity == null || identity.isEmpty()) {
      throw new SubscribingException("Subscriber's identity cannot be null or empty");
    }

    eventBus.subscribe(subscriber);

    final Set<Class<? extends Packet>> packetTypes = new HashSet<>();
    for (final Method method : subscriber.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Subscribe.class)) {
        continue;
      }

      if (method.getParameterCount() == 0) {
        throw new SubscribingException(
            "Subscriber's method %s parameter count is zero".formatted(method.getName()));
      }

      //noinspection unchecked
      final Class<? extends Packet> packetType =
          (Class<? extends Packet>)
              stream(method.getParameterTypes())
                  .filter(Packet.class::isAssignableFrom)
                  .findAny()
                  .orElseThrow();
      packetTypes.add(packetType);
    }

    messageBroker.subscribe(
        identity,
        (channelName, payload) -> {
          final Packet packet = packetSerdes.deserialize(payload);
          final boolean whetherListensForPacket = packetTypes.contains(packet.getClass());
          if (whetherListensForPacket) {
            eventBus.publish(packet, identity);
          }
        });
  }
}
