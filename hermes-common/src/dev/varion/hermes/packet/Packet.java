package dev.varion.hermes.packet;

import static java.util.UUID.randomUUID;

import dev.shiza.dew.event.Event;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public abstract class Packet implements Serializable, Event {

  private UUID uniqueId;
  private String replyChannelName;

  protected Packet(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  protected Packet() {
    this(randomUUID());
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  protected void setUniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getReplyChannelName() {
    return replyChannelName;
  }

  public void setReplyChannelName(final String replyChannelName) {
    this.replyChannelName = replyChannelName;
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

  @Override
  public String toString() {
    try {
      final StringBuilder builder = new StringBuilder();
      builder.append("Packet{");
      for (final Field field : getClass().getDeclaredFields()) {
        builder.append(field.getName()).append("=").append(field.get(this));
      }
      builder.append('}');
      return builder.toString();
    } catch (final IllegalAccessException exception) {
      return super.toString();
    }
  }
}
