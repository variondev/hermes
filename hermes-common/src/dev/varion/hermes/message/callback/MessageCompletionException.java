package dev.varion.hermes.message.callback;

public final class MessageCompletionException extends RuntimeException {

  MessageCompletionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
