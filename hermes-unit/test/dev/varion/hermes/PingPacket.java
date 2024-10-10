package dev.varion.hermes;

import dev.varion.hermes.serdes.jackson.JacksonPacket;

public class PingPacket extends JacksonPacket {

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
