package dev.varion.hermes.callback;

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

final class PacketCallbackService implements PacketCallbackFacade {

  private final Map<UUID, CompletableFuture<?>> responses = new ConcurrentHashMap<>();

  @Override
  public void add(final UUID uniqueId, final CompletableFuture<?> responseFuture) {
    responses.put(uniqueId, responseFuture);
  }

  @Override
  public void remove(final UUID uniqueId) {
    responses.remove(uniqueId);
  }

  @Override
  public Optional<CompletableFuture<?>> findByUniqueId(final UUID uniqueId) {
    return ofNullable(responses.get(uniqueId));
  }
}
