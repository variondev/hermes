package dev.araucaris.hermes.message.pubsub;

import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.codec.MessageCodec;
import dev.shiza.dew.event.EventPublishingException;

@FunctionalInterface
public interface MessagePublisher {

  static MessagePublisher create(
      final MessageBroker messageBroker, final MessageCodec messageCodec) {
    return new MessagePublisherImpl(messageBroker, messageCodec);
  }

  <T extends Message> void publish(String channelName, T packet) throws EventPublishingException;
}
