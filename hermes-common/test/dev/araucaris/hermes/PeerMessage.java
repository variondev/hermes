package dev.araucaris.hermes;

import dev.araucaris.hermes.message.codec.MessagePackMessage;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public class PeerMessage extends MessagePackMessage {

  private String content;

  public PeerMessage() {}

  public PeerMessage(final String content) {
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
