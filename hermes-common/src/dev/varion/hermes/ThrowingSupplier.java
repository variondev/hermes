package dev.varion.hermes;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
  T get() throws E;
}
