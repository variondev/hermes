package dev.varion.hermes.packet.codec;

import dev.varion.hermes.packet.Packet;

public interface PacketCodec {

  Packet deserialize(byte[] serializedData) throws PacketCodecException;

  byte[] serialize(Packet packet) throws PacketCodecException;
}
