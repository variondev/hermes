package dev.varion.hermes.callback;

public final class PacketCompletionException extends RuntimeException {

  PacketCompletionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
