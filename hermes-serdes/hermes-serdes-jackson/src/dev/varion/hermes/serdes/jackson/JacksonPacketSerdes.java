package dev.varion.hermes.serdes.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import dev.varion.hermes.packet.serdes.PacketSerdesException;
import java.io.IOException;

public final class JacksonPacketSerdes implements PacketSerdes {

  private final ObjectMapper mapper;

  public JacksonPacketSerdes(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public static PacketSerdes create(final ObjectMapper objectMapper) {
    return new JacksonPacketSerdes(objectMapper);
  }

  public static PacketSerdes create() {
    return new JacksonPacketSerdes(
        JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .build()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setVisibility(
                VisibilityChecker.Std.defaultInstance()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)));
  }

  @Override
  public Packet deserialize(final byte[] data) throws PacketSerdesException {
    try {
      return mapper.readValue(data, Packet.class);
    } catch (final IOException exception) {
      throw new PacketSerdesException("Could not deserialize packet", exception);
    }
  }

  @Override
  public byte[] serialize(final Packet packet) throws PacketSerdesException {
    try {
      return mapper.writeValueAsBytes(packet);
    } catch (final JsonProcessingException exception) {
      throw new PacketSerdesException("Could not deserialize packet", exception);
    }
  }
}
