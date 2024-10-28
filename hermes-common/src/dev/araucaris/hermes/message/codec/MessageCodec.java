package dev.araucaris.hermes.message.codec;

import dev.araucaris.hermes.message.Message;

public interface MessageCodec {

  Message deserialize(byte[] serializedData) throws MessageCodecException;

  byte[] serialize(Message message) throws MessageCodecException;
}
