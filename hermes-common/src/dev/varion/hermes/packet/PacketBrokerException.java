package dev.varion.hermes.packet;

public class PacketBrokerException extends IllegalStateException {

  public PacketBrokerException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
