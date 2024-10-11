package dev.varion.hermes;

import dev.varion.hermes.packet.serdes.jackson.JacksonPacket;

public class PeerPacket extends JacksonPacket {

  private String content;

  public PeerPacket() {}

  public PeerPacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
