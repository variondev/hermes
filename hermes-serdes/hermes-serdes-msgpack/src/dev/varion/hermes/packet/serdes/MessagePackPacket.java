package dev.varion.hermes.packet.serdes;

import dev.varion.hermes.packet.Packet;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public abstract class MessagePackPacket extends Packet {

  public abstract void write(MessageBufferPacker packer) throws Exception;

  public abstract void read(MessageUnpacker unpacker) throws Exception;
}
