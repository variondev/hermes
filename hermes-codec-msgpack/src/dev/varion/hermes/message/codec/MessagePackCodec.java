package dev.varion.hermes.message.codec;

import static dev.varion.hermes.message.codec.MessagePackUtils.instantiatePacket;
import static dev.varion.hermes.message.codec.MessagePackUtils.resolvePacketType;

import dev.varion.hermes.message.Message;
import java.util.Arrays;
import java.util.UUID;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public final class MessagePackCodec implements MessageCodec {

  private final MessagePackContext messagePackContext;

  MessagePackCodec(final MessagePackContext messagePackContext) {
    this.messagePackContext = messagePackContext;
  }

  public static MessagePackCodec create(final MessagePackContext messagePackContext) {
    return new MessagePackCodec(messagePackContext);
  }

  public static MessagePackCodec create() {
    return create(MessagePackContext.create());
  }

  @Override
  public Message deserialize(final byte[] serializedData) throws MessageCodecException {
    try (final MessageUnpacker unpacker =
        messagePackContext.createMessageUnpacker(serializedData)) {
      final MessagePackMessage packet =
          instantiatePacket(resolvePacketType(unpacker.unpackString()));
      packet.setUniqueId(new UUID(unpacker.unpackLong(), unpacker.unpackLong()));
      packet.read(unpacker);
      return packet;
    } catch (final Exception exception) {
      throw new MessageCodecException(
          "Could not deserialize packet via Jackson, preview %s"
              .formatted(Arrays.toString(serializedData)),
          exception);
    }
  }

  @Override
  public byte[] serialize(final Message message) throws MessageCodecException {
    try {
      final MessagePackMessage messagePackPacket = (MessagePackMessage) message;
      try (final MessageBufferPacker packer = messagePackContext.createMessageBufferPacker()) {
        packer.packString(message.getClass().getName());
        final UUID uniqueId = message.getUniqueId();
        packer.packLong(uniqueId.getMostSignificantBits());
        packer.packLong(uniqueId.getLeastSignificantBits());
        messagePackPacket.write(packer);
        return packer.toByteArray();
      }
    } catch (final ClassCastException exception) {
      throw new MessageCodecException(
          "Could not serialize message via MessagePack, because message %s does not extend MessagePackMessage."
              .formatted(message.getClass()));
    } catch (final Exception exception) {
      throw new MessageCodecException(
          "Could not serialize message via MessagePack, identified by %s"
              .formatted(message.getUniqueId()),
          exception);
    }
  }
}
