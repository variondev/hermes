package dev.varion.hermes.packet;

import static java.util.concurrent.CompletableFuture.completedFuture;

import dev.varion.hermes.HermesListener;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.util.concurrent.CompletionStage;

public final class NatsPacketBroker implements PacketBroker {

  private final Connection connection;

  NatsPacketBroker(final Connection connection) {
    this.connection = connection;
  }

  public static PacketBroker create(final Connection connection) {
    return new NatsPacketBroker(connection);
  }

  public static PacketBroker create(final Options options) throws PacketBrokerException {
    try {
      return create(Nats.connect(options));
    } catch (final Exception exception) {
      throw new PacketBrokerException(
          "Could not initiate a nats connection required for a message broker, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public CompletionStage<Long> publish(final String channelName, final byte[] payload) {
    connection.publish(channelName, payload);
    return completedFuture(0L);
  }

  @Override
  public void subscribe(final String channelName, final HermesListener listener) {
    connection
        .createDispatcher(message -> listener.receive(channelName, message.getData()))
        .subscribe(channelName);
  }

  @Override
  public void close() throws PacketBrokerException {
    try {
      connection.close();
    } catch (final InterruptedException exception) {
      throw new PacketBrokerException(
          "Could not close nats message broker, because of unexpected exception.", exception);
    }
  }
}
