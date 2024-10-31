package dev.varion.hermes.message.callback.requester;

public final class MessageRequestingException extends RuntimeException {

  MessageRequestingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
