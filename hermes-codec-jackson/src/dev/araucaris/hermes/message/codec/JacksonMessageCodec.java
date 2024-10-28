package dev.araucaris.hermes.message.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araucaris.hermes.message.Message;
import java.io.IOException;
import java.util.Arrays;

final class JacksonMessageCodec implements MessageCodec {

  private final ObjectMapper mapper;

  JacksonMessageCodec(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Message deserialize(final byte[] serializedData) throws MessageCodecException {
    try {
      return mapper.readValue(serializedData, JacksonMessage.class);
    } catch (final IOException exception) {
      throw new MessageCodecException(
          "Could not deserialize packet via Jackson, preview %s"
              .formatted(Arrays.toString(serializedData)),
          exception);
    }
  }

  @Override
  public byte[] serialize(final Message message) throws MessageCodecException {
    try {
      return mapper.writeValueAsBytes(message);
    } catch (final JsonProcessingException exception) {
      throw new MessageCodecException(
          "Could not serialize message via Jackson, identified by %s"
              .formatted(message.getUniqueId()),
          exception);
    }
  }
}
