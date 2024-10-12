package dev.varion.hermes.message.pubsub;

import dev.shiza.dew.event.EventPublishingException;
import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.codec.MessageCodec;

@FunctionalInterface
public interface MessagePublisher {

  static MessagePublisher create(
      final MessageBroker messageBroker, final MessageCodec messageCodec) {
    return new MessagePublisherImpl(messageBroker, messageCodec);
  }

  <T extends Message> void publish(String channelName, T packet) throws EventPublishingException;
}
