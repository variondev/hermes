package dev.varion.hermes.distributed;

import dev.varion.hermes.keyvalue.KeyValueStorage;

@FunctionalInterface
public interface DistributedLocks {

  static DistributedLocks create(final KeyValueStorage kvStorage) {
    return new DistributedLocksImpl(kvStorage);
  }

  DistributedLock createLock(String key);
}
