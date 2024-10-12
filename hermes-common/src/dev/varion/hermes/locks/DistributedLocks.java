package dev.varion.hermes.locks;

import dev.varion.hermes.kv.KeyValueStorage;

@FunctionalInterface
public interface DistributedLocks {

  static DistributedLocks create(final KeyValueStorage kvStorage) {
    return new DistributedLocksImpl(kvStorage);
  }

  DistributedLock createLock(String key);
}
