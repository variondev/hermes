package dev.varion.hermes.locks;

public class DistributedLockException extends Exception {

  DistributedLockException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
