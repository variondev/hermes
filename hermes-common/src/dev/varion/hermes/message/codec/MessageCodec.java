package dev.varion.hermes.message.codec;

import dev.varion.hermes.message.Message;

public interface MessageCodec {

  Message deserialize(byte[] serializedData) throws MessageCodecException;

  byte[] serialize(Message message) throws MessageCodecException;
}
