package dev.varion.hermes.packet.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.varion.hermes.packet.Packet;
import java.io.IOException;
import java.util.Arrays;

final class JacksonPacketCodec implements PacketCodec {

  private final ObjectMapper mapper;

  JacksonPacketCodec(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Packet deserialize(final byte[] serializedData) throws PacketCodecException {
    try {
      return mapper.readValue(serializedData, JacksonPacket.class);
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
