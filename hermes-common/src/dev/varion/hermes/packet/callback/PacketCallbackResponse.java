package dev.varion.hermes.packet.callback;

import java.util.UUID;

public interface PacketCallbackResponse {

  UUID getUniqueId();

  void setUniqueId(UUID uuid);
}
