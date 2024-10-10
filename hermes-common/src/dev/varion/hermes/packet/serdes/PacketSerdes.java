package dev.varion.hermes.packet.serdes;

import dev.varion.hermes.packet.Packet;

public interface PacketSerdes {

  Packet deserialize(byte[] data) throws PacketSerdesException;

  byte[] serialize(Packet packet) throws PacketSerdesException;
}
