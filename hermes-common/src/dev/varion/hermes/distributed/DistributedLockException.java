package dev.varion.hermes.distributed;

public class DistributedLockException extends RuntimeException {

  DistributedLockException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
