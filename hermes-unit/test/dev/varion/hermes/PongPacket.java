package dev.varion.hermes;

import dev.varion.hermes.serdes.jackson.JacksonPacket;

public class PongPacket extends JacksonPacket {

  // This can be any type which implements Serializable
  private String message;

  public PongPacket() {}

  public PongPacket(final String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
