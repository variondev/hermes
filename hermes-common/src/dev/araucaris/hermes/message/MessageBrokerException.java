package dev.araucaris.hermes.message;

public class MessageBrokerException extends IllegalStateException {

  public MessageBrokerException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
