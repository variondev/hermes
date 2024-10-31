package dev.varion.hermes.message;

import static java.util.UUID.randomUUID;

import dev.shiza.dew.event.Event;
import java.util.Objects;
import java.util.UUID;

public abstract class Message implements Event {

  private UUID uniqueId;

  protected Message(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  protected Message() {
    this(randomUUID());
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Message dispatchTo(final UUID uniqueId) {
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

    final Message message = (Message) comparedObject;
    return Objects.equals(uniqueId, message.uniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uniqueId);
  }
}
