package dev.araucaris.hermes.message.codec;

import dev.araucaris.hermes.message.Message;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public abstract class MessagePackMessage extends Message {

  public abstract void write(MessageBufferPacker packer) throws Exception;

  public abstract void read(MessageUnpacker unpacker) throws Exception;
}
