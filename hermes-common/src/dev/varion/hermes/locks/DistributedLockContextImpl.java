package dev.varion.hermes.locks;

record DistributedLockContextImpl(String owner, long expiresAt) implements DistributedLockContext {

  @Override
  public String toString() {
    return owner + ":" + expiresAt;
  }
}
