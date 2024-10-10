package dev.varion.hermes.message;

public final class MessageProcessingException extends IllegalStateException {

  public MessageProcessingException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
