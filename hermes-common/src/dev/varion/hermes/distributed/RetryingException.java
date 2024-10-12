package dev.varion.hermes.distributed;

public final class RetryingException extends RuntimeException {

  private final int retryCount;

  RetryingException(final int retryCount) {
    super("Retrying " + retryCount + " times");
    this.retryCount = retryCount;
  }

  public int getRetryCount() {
    return retryCount;
  }
}
