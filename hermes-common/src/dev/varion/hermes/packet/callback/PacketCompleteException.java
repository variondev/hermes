package dev.varion.hermes.packet.callback;

public final class PacketCompleteException extends RuntimeException {

  PacketCompleteException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
