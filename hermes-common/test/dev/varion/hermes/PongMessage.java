package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;

public class PongMessage extends JacksonPacket {

  private String content;

  public PongMessage() {}

  public PongMessage(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
