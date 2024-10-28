package dev.araucaris.hermes;

import dev.araucaris.hermes.distributed.DistributedLockConfig;
import dev.araucaris.hermes.distributed.DistributedLocks;
import dev.araucaris.hermes.keyvalue.KeyValueConfig;
import dev.araucaris.hermes.keyvalue.KeyValueStorage;
import dev.araucaris.hermes.message.MessageBroker;
import dev.araucaris.hermes.message.MessageBrokerConfig;
import dev.araucaris.hermes.message.callback.MessageCallbackConfig;
import dev.araucaris.hermes.message.callback.MessageCallbackFacade;
import dev.araucaris.hermes.message.callback.requester.MessageCallbackRequester;
import dev.araucaris.hermes.message.codec.MessageCodec;
import dev.araucaris.hermes.message.codec.MessageCodecConfig;
import dev.araucaris.hermes.message.pubsub.MessagePublisher;
import dev.araucaris.hermes.message.pubsub.MessageSubscriber;
import dev.shiza.dew.event.EventBus;
import java.util.function.Consumer;

public final class HermesConfigurator {

  private final EventBusConfig eventBusConfig = new EventBusConfig();
  private final MessageBrokerConfig messageBrokerConfig = new MessageBrokerConfig();
  private final MessageCallbackConfig messageCallbackConfig = new MessageCallbackConfig();
  private final MessageCodecConfig messageCodecConfig = new MessageCodecConfig();
  private final KeyValueConfig keyValueConfig = new KeyValueConfig();
  private final DistributedLockConfig distributedLockConfig = new DistributedLockConfig();

  HermesConfigurator() {}

  public static Hermes configure(final Consumer<HermesConfigurator> mutator) {
    final HermesConfigurator configurator = new HermesConfigurator();
    mutator.accept(configurator);

    final MessageBroker messageBroker = configurator.messageBroker().get();
    if (messageBroker == null) {
      throw new HermesException("Message broker is missing while building Hermes.");
    }

    final MessageCodec messageCodec = configurator.messageCodec().get();
    if (messageCodec == null) {
      throw new HermesException("Message codec is missing while building Hermes.");
    }

    final KeyValueStorage keyValueStorage = configurator.keyValue().get();
    final boolean shouldInitializeDistributedLocks =
        configurator.distributedLock().shouldInitializeDistributedLocks();
    DistributedLocks distributedLocks = configurator.distributedLock().get();
    if (keyValueStorage == null && (shouldInitializeDistributedLocks || distributedLocks != null)) {
      throw new HermesException(
          "Key value storage is required when distributed locks are provided.");
    }

    if (shouldInitializeDistributedLocks && distributedLocks == null) {
      distributedLocks = DistributedLocks.create(keyValueStorage);
    }

    final MessagePublisher messagePublisher = MessagePublisher.create(messageBroker, messageCodec);
    final MessageCallbackFacade messageCallbackFacade = MessageCallbackFacade.create();
    return new HermesImpl(
        messageBroker,
        keyValueStorage,
        distributedLocks,
        messagePublisher,
        MessageCallbackRequester.create(
            messageBroker,
            messageCodec,
            messageCallbackFacade,
            configurator.messageCallback().requestCleanupInterval()),
        MessageSubscriber.create(
            configurator.eventBus().get(),
            messageBroker,
            messagePublisher,
            messageCallbackFacade,
            messageCodec));
  }

  public EventBusConfig eventBus() {
    return eventBusConfig;
  }

  public HermesConfigurator eventBus(final Consumer<EventBusConfig> mutator) {
    mutator.accept(eventBusConfig);
    return this;
  }

  public MessageBrokerConfig messageBroker() {
    return messageBrokerConfig;
  }

  public HermesConfigurator messageBroker(final Consumer<MessageBrokerConfig> mutator) {
    mutator.accept(messageBrokerConfig);
    return this;
  }

  public MessageCallbackConfig messageCallback() {
    return messageCallbackConfig;
  }

  public HermesConfigurator messageCallback(final Consumer<MessageCallbackConfig> mutator) {
    mutator.accept(messageCallbackConfig);
    return this;
  }

  public MessageCodecConfig messageCodec() {
    return messageCodecConfig;
  }

  public HermesConfigurator messageCodec(final Consumer<MessageCodecConfig> mutator) {
    mutator.accept(messageCodecConfig);
    return this;
  }

  public KeyValueConfig keyValue() {
    return keyValueConfig;
  }

  public HermesConfigurator keyValue(final Consumer<KeyValueConfig> mutator) {
    mutator.accept(keyValueConfig);
    return this;
  }

  public DistributedLockConfig distributedLock() {
    return distributedLockConfig;
  }

  public HermesConfigurator distributedLock(final Consumer<DistributedLockConfig> mutator) {
    mutator.accept(distributedLockConfig);
    return this;
  }

  public static final class EventBusConfig {

    private final EventBus eventBus;

    public EventBusConfig() {
      eventBus = EventBus.create().publisher(Runnable::run);
    }

    public EventBus get() {
      return eventBus;
    }

    public void using(final Consumer<EventBus> mutator) {
      mutator.accept(eventBus);
    }
  }
}
