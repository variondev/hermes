package dev.araucaris.hermes.futures;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// by github.com/rchomczyk
public final class CompletableFutures {

  // prefer this constructor with zero core threads for a shared pool,
  // to avoid blocking JVM exit
  private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(0);

  private CompletableFutures() {}

  public static CompletableFuture<Void> runAsync(final Runnable runnable, final Duration delay) {
    return CompletableFuture.runAsync(
        runnable, delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS));
  }

  public static <T> CompletableFuture<T> supplyAsync(
      final Supplier<T> supplier, final Duration delay) {
    return CompletableFuture.supplyAsync(
        supplier, delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS));
  }

  public static Executor delayedExecutor(final long delay, final TimeUnit unit) {
    return delayedExecutor(delay, unit, ForkJoinPool.commonPool());
  }

  public static Executor delayedExecutor(
      final long delay, final TimeUnit unit, final Executor executor) {
    return task -> SCHEDULER.schedule(() -> executor.execute(task), delay, unit);
  }
}
