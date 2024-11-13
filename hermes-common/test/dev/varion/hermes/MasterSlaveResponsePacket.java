package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;

public class MasterSlaveResponsePacket extends JacksonPacket {

  private String content;

  public MasterSlaveResponsePacket() {}

  public MasterSlaveResponsePacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
