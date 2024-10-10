package dev.varion.hermes.message;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import io.nats.client.Connection;
import io.nats.client.Message;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

final class NatsMessageBroker implements MessageBroker {

  private static final Logger LOGGER = getLogger(NatsMessageBroker.class.getName());

  private final Connection connection;

  NatsMessageBroker(final Connection connection) {
    this.connection = connection;
  }

  @Override
  public void publish(final String channelName, final byte[] payload) {
    connection.publish(channelName, payload);
  }

  @Override
  public void subscribe(final String channelName, final MessageListener listener) {
    connection
        .createDispatcher(
            message -> listener.receive(channelName, message.getReplyTo(), message.getData()))
        .subscribe(channelName);
  }

  @Override
  public CompletableFuture<byte[]> request(final String channelName, final byte[] payload) {
    return connection
        .request(channelName, payload)
        .thenApply(Message::getData)
        .exceptionally(
            cause -> {
              LOGGER.log(
                  SEVERE,
                  "Request of packet could not been completed over the %s channel with payload of %d bytes. Preview: %s"
                      .formatted(channelName, payload.length, payload));
              return null;
            });
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
