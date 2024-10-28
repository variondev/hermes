package dev.araucaris.hermes.message.callback.requester;

import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.callback.MessageCallbackFacade;
import dev.araucaris.hermes.message.codec.MessageCodec;
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
