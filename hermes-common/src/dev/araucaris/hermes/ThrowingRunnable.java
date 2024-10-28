package dev.araucaris.hermes;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
  void run() throws E;
}
