package dev.araucaris.hermes.message.pubsub;

import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.callback.MessageCallbackFacade;
import dev.araucaris.hermes.message.codec.MessageCodec;
import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;

@FunctionalInterface
public interface MessageSubscriber {

  static MessageSubscriber create(
      final EventBus eventBus,
      final MessageBroker messageBroker,
      final MessagePublisher messagePublisher,
      final MessageCallbackFacade messageCallbackFacade,
      final MessageCodec messageCodec) {
    return new MessageSubscriberImpl(
        eventBus, messageBroker, messagePublisher, messageCallbackFacade, messageCodec);
  }

  void subscribe(Subscriber subscriber) throws MessageSubscribingException;
}
