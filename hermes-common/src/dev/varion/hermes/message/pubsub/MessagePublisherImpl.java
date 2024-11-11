package dev.varion.hermes.message.pubsub;

import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.codec.MessageCodec;
import java.util.concurrent.CompletionStage;

final class MessagePublisherImpl implements MessagePublisher {

  private final MessageBroker messageBroker;
  private final MessageCodec messageCodec;

  MessagePublisherImpl(final MessageBroker messageBroker, final MessageCodec messageCodec) {
    this.messageBroker = messageBroker;
    this.messageCodec = messageCodec;
  }

  @Override
  public <T extends Message> CompletionStage<Long> publish(final String channelName, final T packet) {
    try {
      final byte[] payload = messageCodec.serialize(packet);
      return messageBroker.publish(channelName, payload);
    } catch (final Exception exception) {
      throw new MessagePublishingException(
          "Could not publish packet over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
