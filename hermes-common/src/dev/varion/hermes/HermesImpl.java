package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.kv.KeyValueStorage;
import dev.varion.hermes.locks.DistributedLocks;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.callback.requester.PacketCallbackRequester;
import dev.varion.hermes.packet.pubsub.PacketPublisher;
import dev.varion.hermes.packet.pubsub.PacketSubscriber;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class HermesImpl implements Hermes {

  private final MessageBroker messageBroker;
  private final KeyValueStorage keyValueStorage;
  private final DistributedLocks distributedLocks;
  private final PacketPublisher packetPublisher;
  private final PacketCallbackRequester packetCallbackRequester;
  private final PacketSubscriber packetSubscriber;

  HermesImpl(
      final MessageBroker messageBroker,
      final KeyValueStorage keyValueStorage,
      final DistributedLocks distributedLocks,
      final PacketPublisher packetPublisher,
      final PacketCallbackRequester packetCallbackRequester,
      final PacketSubscriber packetSubscriber) {
    this.messageBroker = messageBroker;
    this.keyValueStorage = keyValueStorage;
    this.distributedLocks = distributedLocks;
    this.packetPublisher = packetPublisher;
    this.packetCallbackRequester = packetCallbackRequester;
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
    return packetCallbackRequester.request(channelName, request);
  }

  @Override
  public KeyValueStorage kv() throws MissingServiceException {
    if (keyValueStorage == null) {
      throw new MissingServiceException("Key value storage has not been provided.");
    }
    return keyValueStorage;
  }

  @Override
  public DistributedLocks locks() throws MissingServiceException {
    if (distributedLocks == null) {
      throw new MissingServiceException("Distributed locks have not been provided.");
    }
    return distributedLocks;
  }

  @Override
  public void close() throws IOException {
    messageBroker.close();
  }
}
