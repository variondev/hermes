package dev.varion.hermes.packet.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import dev.varion.hermes.packet.Packet;
import java.io.IOException;
import java.util.Arrays;

final class JacksonPacketCodec implements PacketCodec {

  private final ObjectMapper mapper;
  private final boolean throwOnUnknownType;

  JacksonPacketCodec(final ObjectMapper mapper, final boolean throwOnUnknownType) {
    this.mapper = mapper;
    this.throwOnUnknownType = throwOnUnknownType;
  }

  @Override
  public Packet deserialize(final byte[] serializedData) throws PacketCodecException {
    try {
      return mapper.readValue(serializedData, JacksonPacket.class);
    } catch (final InvalidTypeIdException exception) {
      if (throwOnUnknownType) {
        throw new PacketCodecException(
            "Type %s could not be found, when deserializing packet via Jackson"
                .formatted(exception.getTypeId()),
            exception);
      }
      return null;
    } catch (final IOException exception) {
      throw new PacketCodecException(
          "Could not deserialize packet via Jackson, preview %s"
              .formatted(Arrays.toString(serializedData)),
          exception);
    }
  }

  @Override
  public byte[] serialize(final Packet packet) throws PacketCodecException {
    try {
      return mapper.writeValueAsBytes(packet);
    } catch (final JsonProcessingException exception) {
      throw new PacketCodecException(
          "Could not serialize packet via Jackson, identified by %s"
              .formatted(packet.getUniqueId()),
          exception);
    }
  }
}
