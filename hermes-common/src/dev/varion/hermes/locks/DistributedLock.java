package dev.varion.hermes.locks;

public interface DistributedLock {
  boolean lock(long ttl) throws DistributedLockException;

  boolean unlock() throws DistributedLockException;
}
