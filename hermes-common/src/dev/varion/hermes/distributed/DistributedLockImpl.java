package dev.varion.hermes.distributed;

import static com.spotify.futures.CompletableFutures.exceptionallyCompose;
import static dev.varion.hermes.functional.CompletableFutures.runAsync;
import static java.lang.Math.min;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.ThreadLocalRandom.current;

import dev.varion.hermes.keyvalue.KeyValueException;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class DistributedLockImpl implements DistributedLock {

  private final String key;
  private final KeyValueStorage kvStorage;
  private final String pid;

  DistributedLockImpl(final String key, final KeyValueStorage kvStorage) {
    this.key = key;
    this.kvStorage = kvStorage;
    pid = UUID.randomUUID().toString();
  }

  @Override
  public boolean acquire(final long ttl) throws DistributedLockException {
    final String lockKey = "lock-" + key;
    try {
      final long now = Instant.now().toEpochMilli();
      final long expiresAt = now + ttl;
      final DistributedLockContext lockContext = new DistributedLockContextImpl(pid, expiresAt);
      final String existingValue = kvStorage.retrieve(lockKey);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.expiresAt() < now) {
          return kvStorage.set(lockKey, lockContext.toString());
        }
        return false; // Lock is still valid and held by someone else
      }
      return kvStorage.set(lockKey, lockContext.toString());
    } catch (final KeyValueException exception) {
      throw new DistributedLockException("Failed to acquire lock", exception);
    }
  }

  @Override
  public boolean release() throws DistributedLockException {
    final String lockKey = "lock-" + key;
    try {
      final String existingValue = kvStorage.retrieve(lockKey);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.owner().equals(pid)) {
          kvStorage.remove(lockKey);
          return true;
        }
      }
      return false;
    } catch (final KeyValueException exception) {
      throw new DistributedLockException("Failed to release lock", exception);
    }
  }

  @Override
  public CompletableFuture<Void> execute(
      final Runnable task, final Duration delay, final Duration until) {
    return execute(task, 0, delay, until, Duration.ZERO, Instant.now().plus(until));
  }

  private CompletableFuture<Void> execute(
      final Runnable action,
      final int retryCount,
      final Duration delay,
      final Duration until,
      final Duration backoffDelay,
      final Instant untilTime) {
    if (Instant.now().isAfter(untilTime)) {
      throw new RetryingException(retryCount);
    }
    return exceptionallyCompose(
            runAsync(
                () -> {
                  if (!acquire(until.toMillis())) {
                    throw new IllegalStateException(
                        "Failed to acquire lock within the specified time.");
                  }
                  action.run();
                  release();
                },
                calculateBackoffDelay(delay, until, retryCount + 1)),
            cause ->
                execute(
                    action,
                    retryCount + 1,
                    delay,
                    until,
                    backoffDelay.plus(calculateBackoffDelay(delay, until, retryCount + 1)),
                    untilTime))
        .toCompletableFuture();
  }

  private Duration calculateBackoffDelay(
      final Duration delay, final Duration until, final int retryCount) {
    final long exponentialDelayMillis =
        min(delay.toMillis() * (1L << retryCount), until.toMillis());
    final long randomPart =
        exponentialDelayMillis / 2 + current().nextInt((int) (exponentialDelayMillis / 2));
    return ofMillis(randomPart);
  }
}
