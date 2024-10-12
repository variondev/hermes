package dev.varion.hermes;

import static java.time.Duration.ofSeconds;

import dev.shiza.dew.event.EventBus;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.callback.MessageCallbackFacade;
import dev.varion.hermes.message.callback.requester.MessageCallbackRequester;
import dev.varion.hermes.message.codec.MessageCodec;
import dev.varion.hermes.message.pubsub.MessagePublisher;
import dev.varion.hermes.message.pubsub.MessageSubscriber;
import java.time.Duration;

public final class HermesBuilder {

  private MessageBroker messageBroker;
  private KeyValueStorage keyValueStorage;
  private DistributedLocks distributedLocks;
  private MessageCodec messageCodec;
  private EventBus eventBus = EventBus.create().publisher(Runnable::run);
  private Duration requestCleanupInterval = ofSeconds(10L);
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

  public HermesBuilder withPacketSerdes(final MessageCodec messageCodec) {
    this.messageCodec = messageCodec;
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
    if (messageCodec == null) {
      throw new HermesException("Message codec is missing while building Hermes.");
    }
    if (keyValueStorage == null && (shouldInitializeDistributedLocks || distributedLocks != null)) {
      throw new HermesException(
          "Key value storage is required when distributed locks are provided.");
    }

    if (shouldInitializeDistributedLocks && distributedLocks == null) {
      distributedLocks = DistributedLocks.create(keyValueStorage);
    }

    final MessageCallbackFacade messageCallbackFacade = MessageCallbackFacade.create();
    final MessagePublisher messagePublisher = MessagePublisher.create(messageBroker, messageCodec);
    final MessageCallbackRequester messageCallbackRequester =
        MessageCallbackRequester.create(
            messageBroker, messageCodec, messageCallbackFacade, requestCleanupInterval);
    final MessageSubscriber messageSubscriber =
        MessageSubscriber.create(
            eventBus, messageBroker, messagePublisher, messageCallbackFacade, messageCodec);
    return new HermesImpl(
        messageBroker,
        keyValueStorage,
        distributedLocks,
        messagePublisher,
        messageCallbackRequester,
        messageSubscriber);
  }
}
