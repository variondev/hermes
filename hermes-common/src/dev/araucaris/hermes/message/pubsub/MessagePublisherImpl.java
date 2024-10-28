package dev.araucaris.hermes.message.pubsub;

import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.codec.MessageCodec;

final class MessagePublisherImpl implements MessagePublisher {

  private final MessageBroker messageBroker;
  private final MessageCodec messageCodec;

  MessagePublisherImpl(final MessageBroker messageBroker, final MessageCodec messageCodec) {
    this.messageBroker = messageBroker;
    this.messageCodec = messageCodec;
  }

  @Override
  public <T extends Message> void publish(final String channelName, final T packet) {
    try {
      final byte[] payload = messageCodec.serialize(packet);
      messageBroker.publish(channelName, payload);
    } catch (final Exception exception) {
      throw new MessagePublishingException(
          "Could not publish packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
