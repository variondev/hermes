package dev.varion.hermes.packet.serdes;

public final class PacketSerdesException extends RuntimeException {

  PacketSerdesException(final String message) {
    super(message);
  }

  PacketSerdesException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
