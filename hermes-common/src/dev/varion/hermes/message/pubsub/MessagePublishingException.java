package dev.varion.hermes.message.pubsub;

public final class MessagePublishingException extends RuntimeException {

  MessagePublishingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
