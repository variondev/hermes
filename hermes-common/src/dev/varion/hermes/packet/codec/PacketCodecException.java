package dev.varion.hermes.packet.codec;

public final class PacketCodecException extends RuntimeException {

  PacketCodecException(final String message) {
    super(message);
  }

  PacketCodecException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
