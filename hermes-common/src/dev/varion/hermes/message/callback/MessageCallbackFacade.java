package dev.varion.hermes.message.callback;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MessageCallbackFacade {

  static MessageCallbackFacade create() {
    return new MessageCallbackService();
  }

  void add(UUID uniqueId, CompletableFuture<?> responseFuture);

  void remove(UUID uniqueId);

  Optional<CompletableFuture<?>> findByUniqueId(UUID uniqueId);
}
