package dev.varion.hermes.functional;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
  void run() throws E;
}
