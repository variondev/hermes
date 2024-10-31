package dev.varion.hermes.message.pubsub;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.callback.MessageCallbackFacade;
import dev.varion.hermes.message.codec.MessageCodec;

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
