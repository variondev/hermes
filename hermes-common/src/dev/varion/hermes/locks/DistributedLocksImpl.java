package dev.varion.hermes.locks;

import dev.varion.hermes.kv.KeyValueStorage;

final class DistributedLocksImpl implements DistributedLocks {
  private final KeyValueStorage kvStorage;

  DistributedLocksImpl(final KeyValueStorage kvStorage) {
    this.kvStorage = kvStorage;
  }

  @Override
  public DistributedLock createLock(final String key) {
    return new DistributedLockImpl(key, kvStorage);
  }
}
