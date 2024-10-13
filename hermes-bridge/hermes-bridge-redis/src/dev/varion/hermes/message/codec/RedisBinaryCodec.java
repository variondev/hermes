package dev.varion.hermes.message.codec;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;

public final class RedisBinaryCodec implements RedisCodec<String, byte[]> {

  public static final RedisBinaryCodec INSTANCE = new RedisBinaryCodec();

  RedisBinaryCodec() {}

  @Override
  public String decodeKey(final ByteBuffer buffer) {
    return UTF_8.decode(buffer).toString();
  }

  @Override
  public byte[] decodeValue(final ByteBuffer buffer) {
    final byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    return bytes;
  }

  @Override
  public ByteBuffer encodeKey(final String value) {
    return wrap(value.getBytes(UTF_8));
  }

  @Override
  public ByteBuffer encodeValue(final byte[] value) {
    return wrap(value);
  }
}
