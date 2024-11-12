package dev.varion.hermes.pubsub;

public final class PacketPublishingException extends RuntimeException {

  PacketPublishingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
