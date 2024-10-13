package dev.varion.hermes.distributed;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface DistributedLock {
  boolean acquire(long ttl) throws DistributedLockException;

  boolean release() throws DistributedLockException;

  CompletableFuture<Void> execute(Runnable task, Duration delay, Duration until)
      throws RetryingException, DistributedLockException;
}
