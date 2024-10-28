package dev.araucaris.hermes.distributed;

public final class DistributedLockConfig {

  private DistributedLocks distributedLocks;
  private boolean shouldInitializeDistributedLocks;

  public DistributedLocks get() {
    return distributedLocks;
  }

  public void using(final DistributedLocks distributedLocks) {
    this.distributedLocks = distributedLocks;
  }

  public boolean shouldInitializeDistributedLocks() {
    return shouldInitializeDistributedLocks;
  }

  public void using(final boolean shouldInitializeDistributedLocks) {
    this.shouldInitializeDistributedLocks = shouldInitializeDistributedLocks;
  }
}
