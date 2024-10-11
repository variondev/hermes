package dev.varion.hermes.packet.callback;

import java.util.UUID;

public interface PacketCallbackRequest {

  UUID getUniqueId();

  void setUniqueId(UUID uuid);
}
