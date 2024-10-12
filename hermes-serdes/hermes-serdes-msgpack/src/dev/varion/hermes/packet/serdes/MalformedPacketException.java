package dev.varion.hermes.packet.serdes;

public final class MalformedPacketException extends RuntimeException {

  MalformedPacketException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
