package dev.varion.hermes.packet;

import static java.util.logging.Level.FINEST;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.SubscribingException;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.MessageProcessingException;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.lang.reflect.Method;
import java.util.Arrays;

final class PacketSubscriberImpl implements PacketSubscriber {

  private final EventBus eventBus;
  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketSerdes packetSerdes;

  PacketSubscriberImpl(
      final EventBus eventBus,
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketSerdes packetSerdes) {
    this.eventBus = eventBus;
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetSerdes = packetSerdes;
    eventBus.result(
        Packet.class,
        packet -> {
          final String replyChannelName = packet.getReplyChannelName();
          packetPublisher.publish(replyChannelName, packet);
          loggerFacade.log(
              FINEST,
              "Received request with %s reply channel and responded with %s packet. Preview: %s",
              replyChannelName,
              packet.getClass().getName(),
              packet);
        });
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    final String identity = subscriber.identity();
    if (identity == null || identity.isEmpty()) {
      throw new SubscribingException("Subscriber's identity cannot be null or empty");
    }

    eventBus.subscribe(subscriber);
    for (final Method method : subscriber.getClass().getDeclaredMethods()) {
      if (method.getParameterCount() == 0) {
        throw new SubscribingException(
            "Subscriber's method %s parameter count is zero".formatted(method.getName()));
      }

      //noinspection unchecked
      final Class<? extends Packet> packetType =
          (Class<? extends Packet>)
              Arrays.stream(method.getParameterTypes())
                  .filter(Packet.class::isAssignableFrom)
                  .findAny()
                  .orElseThrow();

      messageBroker.subscribe(
          identity,
          (ignored, replyChannelName, payload) -> {
            final Packet packet = deserializePacket(payload);
            loggerFacade.log(
                FINEST,
                "Received packet of type %s (%s) from %s channel with %s reply channel. Preview: %s",
                packet.getClass().getName(),
                packet.getUniqueId(),
                identity,
                replyChannelName,
                packet);

            final boolean whetherListensForPacket = packetType.equals(packet.getClass());
            if (whetherListensForPacket) {
              packet.setReplyChannelName(replyChannelName);
              eventBus.publish(packet, identity);
              loggerFacade.log(
                  FINEST,
                  "Received packet of type %s (%s) from %s channel with %s reply channel and forwarded to %s listener. Preview: %s",
                  packet.getClass().getName(),
                  packet.getUniqueId(),
                  identity,
                  replyChannelName,
                  subscriber.getClass().getName(),
                  packet);
            }
          });
    }
  }

  @Override
  public <T extends Packet> T deserializePacket(final byte[] payload)
      throws MessageProcessingException {
    try {
      //noinspection unchecked
      return (T) packetSerdes.deserialize(payload);
    } catch (final Exception exception) {
      throw new MessageProcessingException(
          "Could not process incoming packet, because of unexpected exception.", exception);
    }
  }
}
