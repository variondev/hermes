package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;

public class PingMessage extends JacksonPacket {

  private String content;

  public PingMessage() {}

  public PingMessage(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
