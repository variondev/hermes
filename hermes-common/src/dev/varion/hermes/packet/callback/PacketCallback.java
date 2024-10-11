package dev.varion.hermes.packet.callback;

import java.util.UUID;

public interface PacketCallback {

  UUID getUniqueId();

  void setUniqueId(UUID uuid);
}
