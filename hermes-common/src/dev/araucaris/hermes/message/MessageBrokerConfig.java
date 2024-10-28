package dev.araucaris.hermes.message;

public final class MessageBrokerConfig {

  private MessageBroker messageBroker;

  public MessageBroker get() {
    return messageBroker;
  }

  public void using(final MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
  }
}
