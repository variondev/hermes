package dev.varion.hermes;

import dev.varion.hermes.packet.serdes.MessagePackPacket;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class PingPacket extends MessagePackPacket {

  // This can be any type which implements Serializable
  private String message;

  public PingPacket() {}

  public PingPacket(final String message) {
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
