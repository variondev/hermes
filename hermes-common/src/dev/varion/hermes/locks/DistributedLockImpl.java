package dev.varion.hermes.locks;

import dev.varion.hermes.kv.KeyValueException;
import dev.varion.hermes.kv.KeyValueStorage;
import java.time.Instant;
import java.util.UUID;

final class DistributedLockImpl implements DistributedLock {
  private final String key;
  private final KeyValueStorage kvStorage;
  private final String lockId;

  DistributedLockImpl(final String key, final KeyValueStorage kvStorage) {
    this.key = key;
    this.kvStorage = kvStorage;
    lockId = UUID.randomUUID().toString();
  }

  @Override
  public boolean lock(final long ttl) throws DistributedLockException {
    try {
      final long now = Instant.now().toEpochMilli();
      final long expiresAt = now + ttl;
      final DistributedLockContext lockContext = new DistributedLockContextImpl(lockId, expiresAt);

      if (kvStorage.put(key, lockContext.toString())) {
        return true;
      }

      final String existingValue = kvStorage.get(key);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.expiresAt() < now) {
          kvStorage.put(key, lockContext.toString());
          return true;
        }
      }
      return false;
    } catch (final KeyValueException exception) {
      throw new DistributedLockException("Failed to acquire lock", exception);
    }
  }

  @Override
  public boolean unlock() throws DistributedLockException {
    try {
      final String existingValue = kvStorage.get(key);
      if (existingValue != null) {
        final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
        if (existingContext.owner().equals(lockId)) {
          kvStorage.delete(key);
          return true;
        }
      }
      return false;
    } catch (final KeyValueException e) {
      throw new DistributedLockException("Failed to release lock", e);
    }
  }
}
