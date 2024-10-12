package dev.varion.hermes.packet.serdes;

import static org.msgpack.core.MessagePack.newDefaultBufferPacker;
import static org.msgpack.core.MessagePack.newDefaultUnpacker;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class MessagePackContext {

  protected MessagePackContext() {}

  public static MessagePackContext create() {
    return new MessagePackContext();
  }

  public MessageBufferPacker newPacketPacker() {
    return newDefaultBufferPacker();
  }

  public MessageUnpacker newPacketUnpacker(final byte[] content) {
    return newDefaultUnpacker(content);
  }
}
