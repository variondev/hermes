package dev.varion.hermes.packet.serdes;

import static dev.varion.hermes.packet.serdes.MessagePackUtils.instantiatePacket;
import static dev.varion.hermes.packet.serdes.MessagePackUtils.resolvePacketType;

import dev.varion.hermes.packet.Packet;
import java.util.Arrays;
import java.util.UUID;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public final class MessagePackSerdes implements PacketSerdes {

  private final MessagePackContext messagePackContext;

  MessagePackSerdes(final MessagePackContext messagePackContext) {
    this.messagePackContext = messagePackContext;
  }

  public static MessagePackSerdes create(final MessagePackContext messagePackContext) {
    return new MessagePackSerdes(messagePackContext);
  }

  public static MessagePackSerdes create() {
    return create(MessagePackContext.create());
  }

  @Override
  public Packet deserialize(final byte[] serializedData) throws PacketSerdesException {
    try (final MessageUnpacker unpacker =
        messagePackContext.createMessageUnpacker(serializedData)) {
      final MessagePackPacket packet =
          instantiatePacket(resolvePacketType(unpacker.unpackString()));
      packet.setUniqueId(new UUID(unpacker.unpackLong(), unpacker.unpackLong()));
      packet.read(unpacker);
      return packet;
    } catch (final Exception exception) {
      throw new PacketSerdesException(
          "Could not deserialize packet via Jackson, preview %s"
              .formatted(Arrays.toString(serializedData)),
          exception);
    }
  }

  @Override
  public byte[] serialize(final Packet packet) throws PacketSerdesException {
    try {
      final MessagePackPacket messagePackPacket = (MessagePackPacket) packet;
      try (final MessageBufferPacker packer = messagePackContext.createMessageBufferPacker()) {
        packer.packString(packet.getClass().getName());
        final UUID uniqueId = packet.getUniqueId();
        packer.packLong(uniqueId.getMostSignificantBits());
        packer.packLong(uniqueId.getLeastSignificantBits());
        messagePackPacket.write(packer);
        return packer.toByteArray();
      }
    } catch (final ClassCastException exception) {
      throw new PacketSerdesException(
          "Could not serialize packet via MessagePack, because packet %s does not extend MessagePackPacket."
              .formatted(packet.getClass()));
    } catch (final Exception exception) {
      throw new PacketSerdesException(
          "Could not serialize packet via MessagePack, identified by %s"
              .formatted(packet.getUniqueId()),
          exception);
    }
  }
}
