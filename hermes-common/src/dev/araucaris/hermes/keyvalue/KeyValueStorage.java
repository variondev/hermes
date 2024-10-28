package dev.araucaris.hermes.keyvalue;

import dev.araucaris.hermes.ThrowingRunnable;
import dev.araucaris.hermes.ThrowingSupplier;

public interface KeyValueStorage {
  default <T, E extends Exception> T performSafely(
      final ThrowingSupplier<T, E> action, final String message) throws KeyValueException {
    try {
      return action.get();
    } catch (final Exception exception) {
      throw new KeyValueException(message, exception);
    }
  }

  default <E extends Exception> void performSafely(
      final ThrowingRunnable<E> action, final String message) throws KeyValueException {
    try {
      action.run();
    } catch (final Exception exception) {
      throw new KeyValueException(message, exception);
    }
  }

  boolean set(String key, String value) throws KeyValueException;

  String retrieve(String key) throws KeyValueException;

  boolean remove(String key) throws KeyValueException;
}
