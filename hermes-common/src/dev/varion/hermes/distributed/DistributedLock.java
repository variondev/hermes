package dev.varion.hermes.distributed;

public interface DistributedLock {
  boolean lock(long ttl) throws DistributedLockException;

  boolean unlock() throws DistributedLockException;
}
