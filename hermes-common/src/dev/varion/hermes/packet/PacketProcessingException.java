package dev.varion.hermes.packet;

public final class PacketProcessingException extends IllegalStateException {

  public PacketProcessingException(final String message) {
    super(message);
  }

  public PacketProcessingException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
