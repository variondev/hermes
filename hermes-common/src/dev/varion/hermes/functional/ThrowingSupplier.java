package dev.varion.hermes.functional;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
  T get() throws E;
}
