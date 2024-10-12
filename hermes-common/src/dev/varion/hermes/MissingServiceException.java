package dev.varion.hermes;

public final class MissingServiceException extends IllegalStateException {

  MissingServiceException(final String message) {
    super(message);
  }
}
