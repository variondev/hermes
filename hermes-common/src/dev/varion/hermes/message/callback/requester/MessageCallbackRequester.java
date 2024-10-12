package dev.varion.hermes.message.callback.requester;

import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.callback.MessageCallbackFacade;
import dev.varion.hermes.message.codec.MessageCodec;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface MessageCallbackRequester {

  static MessageCallbackRequester create(
      final MessageBroker messageBroker,
      final MessageCodec messageCodec,
      final MessageCallbackFacade messageCallbackFacade,
      final Duration requestCleanupInterval) {
    return new MessageCallbackRequesterImpl(
        messageBroker, messageCodec, messageCallbackFacade, requestCleanupInterval);
  }

  <T extends Message> CompletableFuture<T> request(String channelName, Message request);
}
