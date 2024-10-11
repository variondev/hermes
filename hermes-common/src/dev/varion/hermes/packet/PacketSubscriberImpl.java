package dev.varion.hermes.packet;

import static java.util.Arrays.stream;
import static java.util.logging.Level.FINEST;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.SubscribingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class PacketSubscriberImpl implements PacketSubscriber {

  private final EventBus eventBus;
  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketProcessor packetProcessor;
  private final PacketSerdes packetSerdes;

  PacketSubscriberImpl(
      final EventBus eventBus,
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketProcessor packetProcessor,
      final PacketSerdes packetSerdes) {
    this.eventBus = eventBus;
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetProcessor = packetProcessor;
    this.packetSerdes = packetSerdes;
    eventBus.result(
        Packet.class,
        packet -> {
          final String replyChannelName = packet.getReplyChannelName();
          if (replyChannelName == null || replyChannelName.isEmpty()) {
            throw new PacketProcessingException(
                "Packet's reply channel name cannot be null or empty %s"
                    .formatted(packet.getClass()));
          }

          loggerFacade.log(
              FINEST,
              "Received request with %s reply channel and responded with %s packet",
              replyChannelName,
              packet.getClass().getName());
          packetPublisher.publish(replyChannelName, packet);
        });
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
        (replyChannelName, payload) -> {
          final Packet packet = packetProcessor.processIncomingPacket(payload);
          final boolean whetherListensForPacket = packetTypes.contains(packet.getClass());
          if (whetherListensForPacket) {
            packet.setReplyChannelName(replyChannelName);
            eventBus.publish(packet, identity);
            loggerFacade.log(
                FINEST,
                "Received packet of type %s (%s) from %s channel with %s reply channel and forwarded to %s listener",
                packet.getClass().getName(),
                packet.getUniqueId(),
                identity,
                replyChannelName,
                subscriber.getClass().getName());
          } else {
            loggerFacade.log(
                FINEST,
                "Received packet of type %s (%s) from %s channel with %s reply channel",
                packet.getClass().getName(),
                packet.getUniqueId(),
                identity,
                replyChannelName);
          }
        });
  }
}
