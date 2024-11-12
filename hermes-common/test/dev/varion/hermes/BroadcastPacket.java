package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;

public class BroadcastPacket extends JacksonPacket {

  private String content;

  public BroadcastPacket() {}

  public BroadcastPacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
