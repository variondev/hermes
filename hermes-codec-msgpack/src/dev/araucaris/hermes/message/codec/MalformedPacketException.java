package dev.araucaris.hermes.message.codec;

public final class MalformedPacketException extends RuntimeException {

  MalformedPacketException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
