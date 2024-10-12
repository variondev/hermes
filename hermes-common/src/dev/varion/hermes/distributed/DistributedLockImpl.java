package dev.varion.hermes.distributed;

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
    public boolean lock(final long ttl) throws DistributedLockException {
        try {
            final long now = Instant.now().toEpochMilli();
            final long expiresAt = now + ttl;
            final DistributedLockContext lockContext = new DistributedLockContextImpl(pid, expiresAt);

            if (kvStorage.set(key, lockContext.toString())) {
                return true;
            }

            final String existingValue = kvStorage.retrieve(key);
            if (existingValue != null) {
                final DistributedLockContext existingContext = DistributedLockContext.parse(existingValue);
                if (existingContext.expiresAt() < now) {
                    kvStorage.set(key, lockContext.toString());
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
}
