package dev.varion.hermes.distributed;

import static com.spotify.futures.CompletableFutures.exceptionallyCompose;
import static dev.varion.hermes.futures.CompletableFutures.runAsync;
import static java.lang.Math.min;
import static java.time.Duration.ofMillis;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

public class DistributedExecutor {

  private final int tries;
  private final Duration delay;
  private final Duration until;

  public DistributedExecutor(final int tries, final Duration delay, final Duration until) {
    this.tries = tries;
    this.delay = delay;
    this.until = until;
  }

  public CompletableFuture<Void> execute(final @NotNull Runnable task) {
    return execute(task, 0, Duration.ZERO);
  }

  // by github.com/rchomczyk
  private CompletableFuture<Void> execute(
      final Runnable action, final int retryCount, final Duration backoffDelay) {
    if (retryCount >= tries) {
      throw new RetryingException(retryCount);
    }

    return exceptionallyCompose(
            runAsync(action, calculateBackoffDelay(retryCount + 1)),
            cause ->
                execute(
                    action,
                    retryCount + 1,
                    backoffDelay.plus(calculateBackoffDelay(retryCount + 1))))
        .toCompletableFuture();
  }

  private Duration calculateBackoffDelay(final int retryCount) {
    final long exponentialDelayMillis =
        min(delay.toMillis() * (1L << retryCount), until.toMillis());
    final long randomPart =
        exponentialDelayMillis / 2
            + ThreadLocalRandom.current().nextInt((int) (exponentialDelayMillis / 2));
    return ofMillis(randomPart);
  }
}
