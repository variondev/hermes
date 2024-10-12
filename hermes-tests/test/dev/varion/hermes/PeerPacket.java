package dev.varion.hermes;

import dev.varion.hermes.packet.serdes.MessagePackPacket;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class PeerPacket extends MessagePackPacket {

  private String content;

  public PeerPacket() {}

  public PeerPacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  @Override
  public void write(final MessageBufferPacker packer) throws Exception {
    packer.packString(content);
  }

  @Override
  public void read(final MessageUnpacker unpacker) throws Exception {
    content = unpacker.unpackString();
  }
}
