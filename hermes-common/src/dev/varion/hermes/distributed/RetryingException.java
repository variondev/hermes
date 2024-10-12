package dev.varion.hermes.distributed;

public final class RetryingException extends RuntimeException {

  RetryingException(final int retryCount) {
    super("Retrying " + retryCount + " times");
  }
}
