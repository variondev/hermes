package dev.varion.hermes.callback;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PacketCallbackFacade {

  static PacketCallbackFacade create() {
    return new PacketCallbackService();
  }

  void add(UUID uniqueId, CompletableFuture<?> responseFuture);

  void remove(UUID uniqueId);

  Optional<CompletableFuture<?>> findByUniqueId(UUID uniqueId);
}
