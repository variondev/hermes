package dev.varion.hermes.packet;

import static java.util.UUID.randomUUID;

import dev.shiza.dew.event.Event;
import java.util.Objects;
import java.util.UUID;

public abstract class Packet implements Event {

  private UUID uniqueId;

  protected Packet(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  protected Packet() {
    this(randomUUID());
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Packet dispatchTo(final UUID uniqueId) {
    this.uniqueId = uniqueId;
    return this;
  }

  @Override
  public boolean equals(final Object comparedObject) {
    if (this == comparedObject) {
      return true;
    }

    if (comparedObject == null || getClass() != comparedObject.getClass()) {
      return false;
    }

    final Packet packet = (Packet) comparedObject;
    return Objects.equals(uniqueId, packet.uniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uniqueId);
  }
}
