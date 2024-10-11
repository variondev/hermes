package dev.varion.hermes;

import dev.varion.hermes.packet.callback.PacketCallback;
import dev.varion.hermes.packet.serdes.jackson.JacksonPacket;

public class PingPacket extends JacksonPacket implements PacketCallback {

  // This can be any type which implements Serializable
  private String message;

  public PingPacket() {}

  public PingPacket(final String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
