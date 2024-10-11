package dev.varion.hermes;


import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketPublisher;
import dev.varion.hermes.packet.PacketRequester;
import dev.varion.hermes.packet.PacketSubscriber;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class HermesImpl implements Hermes {

  private final MessageBroker messageBroker;
  private final PacketPublisher packetPublisher;
  private final PacketRequester packetRequester;
  private final PacketSubscriber packetSubscriber;

  HermesImpl(
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketRequester packetRequester,
      final PacketSubscriber packetSubscriber) {
    this.messageBroker = messageBroker;
    this.packetPublisher = packetPublisher;
    this.packetRequester = packetRequester;
    this.packetSubscriber = packetSubscriber;
  }

  @Override
  public <T extends Packet> void publish(final String channelName, final T packet) {
    packetPublisher.publish(channelName, packet);
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    packetSubscriber.subscribe(subscriber);
  }

  @Override
  public <T extends Packet> CompletableFuture<T> request(
      final String channelName, final Packet request) {
    return packetRequester.request(channelName, request);
  }

  @Override
  public void close() throws IOException {
    messageBroker.close();
  }
}
