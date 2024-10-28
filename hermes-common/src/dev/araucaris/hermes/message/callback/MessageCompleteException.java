package dev.araucaris.hermes.message.callback;

public final class MessageCompleteException extends RuntimeException {

  MessageCompleteException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
