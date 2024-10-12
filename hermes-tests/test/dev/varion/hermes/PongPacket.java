package dev.varion.hermes;

import dev.varion.hermes.packet.serdes.MessagePackPacket;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class PongPacket extends MessagePackPacket {

  private String message;

  public PongPacket() {}

  public PongPacket(final String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void write(final MessageBufferPacker packer) throws Exception {
    packer.packString(message);
  }

  @Override
  public void read(final MessageUnpacker unpacker) throws Exception {
    message = unpacker.unpackString();
  }
}
