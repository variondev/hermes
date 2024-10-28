package dev.araucaris.hermes.message.pubsub;

import static java.util.Arrays.stream;

import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.callback.MessageCallbackFacade;
import dev.araucaris.hermes.message.callback.MessageCallbackSubscriber;
import dev.araucaris.hermes.message.codec.MessageCodec;
import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class MessageSubscriberImpl implements MessageSubscriber {

  private final EventBus eventBus;
  private final MessageBroker messageBroker;
  private final MessagePublisher messagePublisher;
  private final MessageCallbackFacade messageCallbackFacade;
  private final MessageCodec messageCodec;

  MessageSubscriberImpl(
      final EventBus eventBus,
      final MessageBroker messageBroker,
      final MessagePublisher messagePublisher,
      final MessageCallbackFacade messageCallbackFacade,
      final MessageCodec messageCodec) {
    this.eventBus = eventBus;
    this.messageBroker = messageBroker;
    this.messagePublisher = messagePublisher;
    this.messageCallbackFacade = messageCallbackFacade;
    this.messageCodec = messageCodec;
    attachResultPacketHandler();
    subscribeToCallbacks();
  }

  @Override
  public void subscribe(final Subscriber subscriber) throws MessageSubscribingException {
    final String identity = subscriber.identity();
    if (identity == null || identity.isEmpty()) {
      throw new MessageSubscribingException("Subscriber's identity cannot be null or empty");
    }

    eventBus.subscribe(subscriber);

    final Set<Class<? extends Message>> packetTypes = new HashSet<>();
    for (final Method method : subscriber.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Subscribe.class)) {
        continue;
      }

      if (method.getParameterCount() == 0) {
        throw new MessageSubscribingException(
            "Subscriber's method %s parameter count is zero".formatted(method.getName()));
      }

      //noinspection unchecked
      final Class<? extends Message> packetType =
          (Class<? extends Message>)
              stream(method.getParameterTypes())
                  .filter(Message.class::isAssignableFrom)
                  .findAny()
                  .orElseThrow();
      packetTypes.add(packetType);
    }

    messageBroker.subscribe(
        identity,
        (channelName, payload) -> {
          final Message message = messageCodec.deserialize(payload);
          final boolean whetherListensForPacket = packetTypes.contains(message.getClass());
          if (whetherListensForPacket) {
            eventBus.publish(message, identity);
          }
        });
  }

  private void subscribeToCallbacks() {
    messageBroker.subscribe(
        "callbacks", MessageCallbackSubscriber.create(messageCodec, messageCallbackFacade));
  }

  private void attachResultPacketHandler() {
    eventBus.result(Message.class, packet -> messagePublisher.publish("callbacks", packet));
  }
}
