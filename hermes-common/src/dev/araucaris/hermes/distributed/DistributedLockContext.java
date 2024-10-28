package dev.araucaris.hermes.distributed;

import static java.lang.Long.parseLong;

public interface DistributedLockContext {

  static DistributedLockContext parse(final String value) {
    final String[] parts = value.split(":");
    return new DistributedLockContextImpl(parts[0], parseLong(parts[1]));
  }

  String owner();

  long expiresAt();

  String toString();
}
