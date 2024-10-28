package dev.araucaris.hermes.message;

import dev.araucaris.hermes.HermesListener;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

public final class NatsMessageBroker implements MessageBroker {

  private final Connection connection;

  NatsMessageBroker(final Connection connection) {
    this.connection = connection;
  }

  public static MessageBroker create(final Connection connection) {
    return new NatsMessageBroker(connection);
  }

  public static MessageBroker create(final Options options) throws MessageBrokerException {
    try {
      return create(Nats.connect(options));
    } catch (final Exception exception) {
      throw new MessageBrokerException(
          "Could not initiate a nats connection required for a message broker, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public void publish(final String channelName, final byte[] payload) {
    connection.publish(channelName, payload);
  }

  @Override
  public void subscribe(final String channelName, final HermesListener listener) {
    connection
        .createDispatcher(message -> listener.receive(channelName, message.getData()))
        .subscribe(channelName);
  }

  @Override
  public void close() throws MessageBrokerException {
    try {
      connection.close();
    } catch (final InterruptedException exception) {
      throw new MessageBrokerException(
          "Could not close nats message broker, because of unexpected exception.", exception);
    }
  }
}
