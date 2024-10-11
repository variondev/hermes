package dev.varion.hermes;

import dev.varion.hermes.packet.Packet;

@FunctionalInterface
public interface HermesListener {

  void receive(String channelName, Packet packet);
}
