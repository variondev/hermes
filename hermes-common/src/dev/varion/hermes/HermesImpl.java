package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.callback.requester.PacketCallbackRequester;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.pubsub.PacketPublisher;
import dev.varion.hermes.pubsub.PacketSubscriber;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class HermesImpl implements Hermes {

  private final PacketBroker packetBroker;
  private final KeyValueStorage keyValueStorage;
  private final DistributedLocks distributedLocks;
  private final PacketPublisher packetPublisher;
  private final PacketCallbackRequester packetCallbackRequester;
  private final PacketSubscriber packetSubscriber;

  HermesImpl(
      final PacketBroker packetBroker,
      final KeyValueStorage keyValueStorage,
      final DistributedLocks distributedLocks,
      final PacketPublisher packetPublisher,
      final PacketCallbackRequester packetCallbackRequester,
      final PacketSubscriber packetSubscriber) {
    this.packetBroker = packetBroker;
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
  public KeyValueStorage keyValue() throws MissingServiceException {
    if (keyValueStorage == null) {
      throw new MissingServiceException("Key value storage has not been provided.");
    }
    return keyValueStorage;
  }

  @Override
  public DistributedLocks distributedLocks() throws MissingServiceException {
    if (distributedLocks == null) {
      throw new MissingServiceException("Distributed locks have not been provided.");
    }
    return distributedLocks;
  }

  @Override
  public void close() throws IOException {
    packetBroker.close();
  }
}
