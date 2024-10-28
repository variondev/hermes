package dev.araucaris.hermes.message.codec;

import static org.msgpack.core.MessagePack.newDefaultBufferPacker;
import static org.msgpack.core.MessagePack.newDefaultUnpacker;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class MessagePackContext {

  protected MessagePackContext() {}

  public static MessagePackContext create() {
    return new MessagePackContext();
  }

  public MessageBufferPacker createMessageBufferPacker() {
    return newDefaultBufferPacker();
  }

  public MessageUnpacker createMessageUnpacker(final byte[] serializedData) {
    return newDefaultUnpacker(serializedData);
  }
}
