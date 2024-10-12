package dev.varion.hermes.message.callback.requester;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.callback.MessageCallbackFacade;
import dev.varion.hermes.message.codec.MessageCodec;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class MessageCallbackRequesterImpl implements MessageCallbackRequester {

  private final MessageBroker messageBroker;
  private final MessageCodec messageCodec;
  private final MessageCallbackFacade messageCallbackFacade;
  private final Duration requestCleanupInterval;

  MessageCallbackRequesterImpl(
      final MessageBroker messageBroker,
      final MessageCodec messageCodec,
      final MessageCallbackFacade messageCallbackFacade,
      final Duration requestCleanupInterval) {
    this.messageBroker = messageBroker;
    this.messageCodec = messageCodec;
    this.messageCallbackFacade = messageCallbackFacade;
    this.requestCleanupInterval = requestCleanupInterval;
  }

  @Override
  public <T extends Message> CompletableFuture<T> request(
      final String channelName, final Message message) {
    final UUID uniqueId = message.getUniqueId();
    try {
      final CompletableFuture<T> completableFuture = new CompletableFuture<>();
      messageBroker.publish(channelName, messageCodec.serialize(message));
      messageCallbackFacade.add(uniqueId, completableFuture);
      return completableFuture.orTimeout(requestCleanupInterval.toMillis(), MILLISECONDS);
    } catch (final Exception exception) {
      throw new MessageRequestingException(
          "Could not request message over the message broker, because of unexpected exception.",
          exception);
    }
  }
}
