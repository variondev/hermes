package dev.varion.hermes;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.kv.KeyValueStorage;
import dev.varion.hermes.locks.DistributedLocks;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.callback.PacketCallbackFacade;
import dev.varion.hermes.packet.callback.requester.PacketCallbackRequester;
import dev.varion.hermes.packet.pubsub.PacketPublisher;
import dev.varion.hermes.packet.pubsub.PacketSubscriber;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.io.Closeable;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface Hermes extends Closeable {

  static HermesBuilder newBuilder() {
    return new HermesBuilder();
  }

  <T extends Packet> void publish(String channelName, T packet);

  void subscribe(Subscriber subscriber);

  <T extends Packet> CompletableFuture<T> request(String channelName, Packet request);

  KeyValueStorage kv() throws MissingServiceException;

  DistributedLocks locks() throws MissingServiceException;

  final class HermesBuilder {

    private MessageBroker messageBroker;
    private KeyValueStorage keyValueStorage;
    private DistributedLocks distributedLocks;
    private PacketSerdes packetSerdes;
    private EventBus eventBus = EventBus.create().publisher(Runnable::run);
    private Duration requestCleanupInterval = Duration.ofSeconds(10L);
    private boolean shouldInitializeDistributedLocks;

    HermesBuilder() {}

    public HermesBuilder withMessageBroker(final MessageBroker messageBroker) {
      this.messageBroker = messageBroker;
      return this;
    }

    public HermesBuilder withKeyValueStorage(final KeyValueStorage keyValueStorage) {
      this.keyValueStorage = keyValueStorage;
      return this;
    }

    public HermesBuilder withDistributedLocks(final DistributedLocks distributedLocks) {
      this.distributedLocks = distributedLocks;
      return this;
    }

    public HermesBuilder withDistributedLocks(final boolean shouldInitializeDistributedLocks) {
      this.shouldInitializeDistributedLocks = shouldInitializeDistributedLocks;
      return this;
    }

    public HermesBuilder withPacketSerdes(final PacketSerdes packetSerdes) {
      this.packetSerdes = packetSerdes;
      return this;
    }

    public HermesBuilder withEventBus(final EventBus eventBus) {
      this.eventBus = eventBus;
      return this;
    }

    public HermesBuilder withRequestCleanupInterval(final Duration requestCleanupInterval) {
      this.requestCleanupInterval = requestCleanupInterval;
      return this;
    }

    public Hermes build() throws HermesException {
      if (messageBroker == null) {
        throw new HermesException("Message broker is missing while building Hermes.");
      }
      if (packetSerdes == null) {
        throw new HermesException("Packet serdes is missing while building Hermes.");
      }
      if (keyValueStorage == null
          && (shouldInitializeDistributedLocks || distributedLocks != null)) {
        throw new HermesException(
            "Key value storage is required when distributed locks are provided.");
      }

      if (shouldInitializeDistributedLocks && distributedLocks == null) {
        distributedLocks = DistributedLocks.create(keyValueStorage);
      }

      final PacketCallbackFacade packetCallbackFacade = PacketCallbackFacade.create();
      final PacketPublisher packetPublisher = PacketPublisher.create(messageBroker, packetSerdes);
      final PacketCallbackRequester packetCallbackRequester =
          PacketCallbackRequester.create(
              messageBroker, packetSerdes, packetCallbackFacade, requestCleanupInterval);
      final PacketSubscriber packetSubscriber =
          PacketSubscriber.create(
              eventBus, messageBroker, packetPublisher, packetCallbackFacade, packetSerdes);
      return new HermesImpl(
          messageBroker,
          keyValueStorage,
          distributedLocks,
          packetPublisher,
          packetCallbackRequester,
          packetSubscriber);
    }
  }
}
