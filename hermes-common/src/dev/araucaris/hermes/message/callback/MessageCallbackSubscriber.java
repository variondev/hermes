package dev.araucaris.hermes.message.callback;

import dev.araucaris.hermes.HermesListener;
import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.codec.MessageCodec;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class MessageCallbackSubscriber implements HermesListener {

  private final MessageCodec messageCodec;
  private final MessageCallbackFacade messageCallbackFacade;

  MessageCallbackSubscriber(
      final MessageCodec messageCodec, final MessageCallbackFacade messageCallbackFacade) {
    this.messageCodec = messageCodec;
    this.messageCallbackFacade = messageCallbackFacade;
  }

  public static MessageCallbackSubscriber create(
      final MessageCodec messageCodec, final MessageCallbackFacade messageCallbackFacade) {
    return new MessageCallbackSubscriber(messageCodec, messageCallbackFacade);
  }

  @Override
  public void receive(final String channelName, final byte[] payload) {
    final Message message = messageCodec.deserialize(payload);
    final UUID uniqueId = message.getUniqueId();
    messageCallbackFacade
        .findByUniqueId(uniqueId)
        .ifPresent(
            future -> {
              try {
                //noinspection unchecked
                ((CompletableFuture<Message>) future).complete(message);
                messageCallbackFacade.remove(uniqueId);
              } catch (final Exception exception) {
                throw new MessageCompleteException(
                    "Failed to complete message identified by %s".formatted(uniqueId), exception);
              }
            });
  }
}
