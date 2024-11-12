package dev.varion.hermes.packet.codec;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public final class MsgpackJacksonObjectMapperFactory {

  private MsgpackJacksonObjectMapperFactory() {}

  public static ObjectMapper getMsgpackJacksonObjectMapper() {
    final ObjectMapper mapper =
        new ObjectMapper(new MessagePackFactory())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    mapper.setVisibility(
        VisibilityChecker.Std.defaultInstance()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    return mapper;
  }
}
