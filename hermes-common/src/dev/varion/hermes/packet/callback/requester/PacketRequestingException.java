package dev.varion.hermes.packet.callback.requester;

public final class PacketRequestingException extends RuntimeException {

  PacketRequestingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
