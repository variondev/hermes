package dev.varion.hermes.packet;

import static java.util.Arrays.stream;
import static java.util.logging.Level.FINEST;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.SubscribingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.callback.PacketCallbackSubscriber;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final class PacketSubscriberImpl implements PacketSubscriber {

  private final EventBus eventBus;
  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;

  PacketSubscriberImpl(
      final EventBus eventBus,
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketCallbackFacade packetCallbackFacade) {
    this.eventBus = eventBus;
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    eventBus.result(
        Packet.class,
        packet -> {
          loggerFacade.log(
              FINEST, "Received request and responded with %s packet", packet.getClass().getName());
          packetPublisher.publish("callbacks", packet);
        });
    messageBroker.subscribe(
        "callbacks", PacketCallbackSubscriber.create(loggerFacade, packetCallbackFacade));
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
        (channelName, packet) -> {
          final UUID uniqueId = packet.getUniqueId();
          final boolean whetherListensForPacket = packetTypes.contains(packet.getClass());
          if (whetherListensForPacket) {
            eventBus.publish(packet, identity);
            loggerFacade.log(
                FINEST,
                "Received packet of type %s (%s) from %s channel with %s reply channel and forwarded to %s listener",
                packet.getClass().getName(),
                packet.getUniqueId(),
                identity,
                uniqueId,
                subscriber.getClass().getName());
          } else {
            loggerFacade.log(
                FINEST,
                "Received packet of type %s (%s) from %s channel with %s reply channel",
                packet.getClass().getName(),
                packet.getUniqueId(),
                identity,
                uniqueId);
          }
        });
  }
}
