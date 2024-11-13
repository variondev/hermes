package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;

public class MasterSlaveRequestPacket extends JacksonPacket {

  private String content;

  public MasterSlaveRequestPacket() {}

  public MasterSlaveRequestPacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
