package dev.varion.hermes.distributed;

import static java.lang.Thread.sleep;

import dev.varion.hermes.keyvalue.KeyValueException;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import java.time.Instant;
import java.util.UUID;

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
  public synchronized boolean lock(final long ttl) throws DistributedLockException {
    final long now = Instant.now().toEpochMilli();
    final long expiresAt = now + ttl;
    final DistributedLockContext lockContext = new DistributedLockContextImpl(pid, expiresAt);

    try {
      while (true) {

        final String existingValue = kvStorage.retrieve(key);
        if (existingValue == null) {
          kvStorage.set(key, lockContext.toString());
          return true;
        }

        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (now > existingContext.expiresAt() && kvStorage.set(key, lockContext.toString())) {
          return true;
        }

        sleep(100);
      }
    } catch (final KeyValueException | InterruptedException exception) {
      throw new DistributedLockException("Failed to acquire lock", exception);
    }
  }

  @Override
  public boolean unlock() throws DistributedLockException {
    try {
      final String existingValue = kvStorage.retrieve(key);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.owner().equals(pid)) {
          kvStorage.remove(key);
          return true;
        }
      }
      return false;
    } catch (final KeyValueException e) {
      throw new DistributedLockException("Failed to release lock", e);
    }
  }

  @Override
  public boolean renew(final long additionalTtl) throws DistributedLockException {
    final long now = Instant.now().toEpochMilli();
    final long newExpiry = now + additionalTtl;

    try {
      final String existingValue = kvStorage.retrieve(key);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.owner().equals(pid)) {
          final DistributedLockContext newContext = new DistributedLockContextImpl(pid, newExpiry);
          kvStorage.set(key, newContext.toString());
          return true;
        }
      }
      return false;
    } catch (final KeyValueException e) {
      throw new DistributedLockException("Failed to renew lock", e);
    }
  }
}
