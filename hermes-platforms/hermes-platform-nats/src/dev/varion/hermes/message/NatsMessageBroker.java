package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

public final class NatsMessageBroker implements MessageBroker {

  private final Connection connection;
  private final PacketSerdes packetSerdes;

  NatsMessageBroker(final Connection connection, final PacketSerdes packetSerdes) {
    this.connection = connection;
    this.packetSerdes = packetSerdes;
  }

  public static MessageBroker create(final Connection connection, final PacketSerdes packetSerdes) {
    return new NatsMessageBroker(connection, packetSerdes);
  }

  public static MessageBroker create(final Options options, final PacketSerdes packetSerdes)
      throws MessageBrokerException {
    try {
      return create(Nats.connect(options), packetSerdes);
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
        .createDispatcher(
            message -> listener.receive(channelName, packetSerdes.deserialize(message.getData())))
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
