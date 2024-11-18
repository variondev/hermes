package dev.varion.hermes.pubsub;

import static java.util.Arrays.stream;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.callback.PacketCallbackFacade;
import dev.varion.hermes.callback.PacketCallbackSubscriber;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.codec.PacketCodec;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class PacketSubscriberImpl implements PacketSubscriber {

  private final EventBus eventBus;
  private final PacketBroker packetBroker;
  private final PacketPublisher packetPublisher;
  private final PacketCallbackFacade packetCallbackFacade;
  private final PacketCodec packetCodec;

  PacketSubscriberImpl(
      final EventBus eventBus,
      final PacketBroker packetBroker,
      final PacketPublisher packetPublisher,
      final PacketCallbackFacade packetCallbackFacade,
      final PacketCodec packetCodec) {
    this.eventBus = eventBus;
    this.packetBroker = packetBroker;
    this.packetPublisher = packetPublisher;
    this.packetCallbackFacade = packetCallbackFacade;
    this.packetCodec = packetCodec;
    attachResultPacketHandler();
    subscribeToCallbacks();
  }

  @Override
  public void subscribe(final Subscriber subscriber) throws PacketSubscribingException {
    final String identity = subscriber.identity();
    if (identity == null || identity.isEmpty()) {
      throw new PacketSubscribingException("Subscriber's identity cannot be null or empty");
    }

    eventBus.subscribe(subscriber);

    final Set<Class<? extends Packet>> packetTypes = new HashSet<>();
    for (final Method method : subscriber.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Subscribe.class)) {
        continue;
      }

      if (method.getParameterCount() == 0) {
        throw new PacketSubscribingException(
            "Subscriber's method %s parameter count is zero".formatted(method.getName()));
      }

      //noinspection unchecked
      final Class<? extends Packet> packetType =
          (Class<? extends Packet>)
              stream(method.getParameterTypes())
                  .filter(Packet.class::isAssignableFrom)
                  .findAny()
                  .orElseThrow(
                      () ->
                          new NullPointerException(
                              "No valid message type found under %s#%s"
                                  .formatted(subscriber.getClass(), method)));
      packetTypes.add(packetType);
    }

    packetBroker.subscribe(
        identity,
        (channelName, payload) -> {
          final Packet packet = packetCodec.deserialize(payload);
          if (packet == null) {
            return;
          }

          final boolean whetherListensForPacket = packetTypes.contains(packet.getClass());
          if (whetherListensForPacket) {
            eventBus.publish(packet, identity);
          }
        });
  }

  private void subscribeToCallbacks() {
    packetBroker.subscribe(
        "callbacks", PacketCallbackSubscriber.create(packetCodec, packetCallbackFacade));
  }

  private void attachResultPacketHandler() {
    eventBus.result(Packet.class, (event, packet) -> packetPublisher.publish("callbacks", packet));
  }
}
