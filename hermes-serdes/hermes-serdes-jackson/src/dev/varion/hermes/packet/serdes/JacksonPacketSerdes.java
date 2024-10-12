package dev.varion.hermes.packet.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.varion.hermes.packet.Packet;
import java.io.IOException;
import java.util.Arrays;

final class JacksonPacketSerdes implements PacketSerdes {

  private final ObjectMapper mapper;

  JacksonPacketSerdes(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Packet deserialize(final byte[] serializedData) throws PacketSerdesException {
    try {
      return mapper.readValue(serializedData, JacksonPacket.class);
    } catch (final IOException exception) {
      throw new PacketSerdesException(
          "Could not deserialize packet via Jackson, preview %s"
              .formatted(Arrays.toString(serializedData)),
          exception);
    }
  }

  @Override
  public byte[] serialize(final Packet packet) throws PacketSerdesException {
    try {
      return mapper.writeValueAsBytes(packet);
    } catch (final JsonProcessingException exception) {
      throw new PacketSerdesException(
          "Could not serialize packet via Jackson, identified by %s"
              .formatted(packet.getUniqueId()),
          exception);
    }
  }
}
