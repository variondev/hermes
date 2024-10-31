package dev.varion.hermes.message.codec;

import java.lang.reflect.InvocationTargetException;

final class MessagePackUtils {

  private MessagePackUtils() {}

  static <T extends MessagePackMessage> Class<T> resolvePacketType(final String className)
      throws MessageDecodingException {
    try {
      // noinspection unchecked
      return (Class<T>) Class.forName(className);
    } catch (final ClassNotFoundException exception) {
      throw new MessageDecodingException(
          "Message definition seems to be malformed, as packet type could not be found in classpath.",
          exception);
    }
  }

  static <T extends MessagePackMessage> T instantiatePacket(final Class<T> packetType)
      throws MessageDecodingException {
    try {
      return packetType.getDeclaredConstructor().newInstance();
    } catch (final NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException exception) {
      throw new MessageDecodingException(
          "Message could not be produced, because of missing public constructor without any parameters.",
          exception);
    }
  }
}
