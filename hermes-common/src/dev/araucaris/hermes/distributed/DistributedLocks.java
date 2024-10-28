package dev.araucaris.hermes.distributed;

import dev.araucaris.hermes.keyvalue.KeyValueStorage;

@FunctionalInterface
public interface DistributedLocks {

  static DistributedLocks create(final KeyValueStorage kvStorage) {
    return new DistributedLocksImpl(kvStorage);
  }

  DistributedLock createLock(String key);
}
