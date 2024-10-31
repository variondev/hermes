package dev.varion.hermes.distributed;

record DistributedLockContextImpl(String owner, long expiresAt) implements DistributedLockContext {

  @Override
  public String toString() {
    return owner + ":" + expiresAt;
  }
}
