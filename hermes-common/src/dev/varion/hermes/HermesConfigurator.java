package dev.varion.hermes;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.event.EventBusFactory;
import dev.varion.hermes.callback.PacketCallbackConfig;
import dev.varion.hermes.callback.PacketCallbackFacade;
import dev.varion.hermes.callback.requester.PacketCallbackRequester;
import dev.varion.hermes.distributed.DistributedLockConfig;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueConfig;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.packet.PacketBroker;
import dev.varion.hermes.packet.PacketBrokerConfig;
import dev.varion.hermes.packet.codec.PacketCodec;
import dev.varion.hermes.packet.codec.PacketCodecConfig;
import dev.varion.hermes.pubsub.PacketPublisher;
import dev.varion.hermes.pubsub.PacketSubscriber;
import java.util.function.Consumer;

public final class HermesConfigurator {

  private final EventBusConfig eventBusConfig = new EventBusConfig();
  private final PacketBrokerConfig packetBrokerConfig = new PacketBrokerConfig();
  private final PacketCallbackConfig packetCallbackConfig = new PacketCallbackConfig();
  private final PacketCodecConfig packetCodecConfig = new PacketCodecConfig();
  private final KeyValueConfig keyValueConfig = new KeyValueConfig();
  private final DistributedLockConfig distributedLockConfig = new DistributedLockConfig();

  HermesConfigurator() {}

  public static Hermes configure(final Consumer<HermesConfigurator> mutator) {
    final HermesConfigurator configurator = new HermesConfigurator();
    mutator.accept(configurator);

    final PacketBroker packetBroker = configurator.packetBroker().get();
    if (packetBroker == null) {
      throw new HermesException("Packet broker is missing while building Hermes.");
    }

    final PacketCodec packetCodec = configurator.packetCodec().get();
    if (packetCodec == null) {
      throw new HermesException("Packet codec is missing while building Hermes.");
    }

    final PacketCallbackConfig callbackConfig = configurator.packetCallback();
    final PacketCallbackFacade packetCallbackFacade = callbackConfig.packetCallbackFacade();
    final PacketCallbackRequester packetCallbackRequester =
        PacketCallbackRequester.create(
            packetBroker,
            packetCodec,
            packetCallbackFacade,
            callbackConfig.requestCleanupInterval());
    final PacketPublisher packetPublisher = PacketPublisher.create(packetBroker, packetCodec);
    final PacketSubscriber packetSubscriber =
        PacketSubscriber.create(
            configurator.eventBus().get(),
            packetBroker,
            packetPublisher,
            packetCallbackFacade,
            packetCodec);
    final KeyValueStorage keyValueStorage = configurator.keyValue().get();
    final DistributedLocks distributedLocks =
        configureDistributedLocks(configurator, keyValueStorage);
    return new HermesImpl(
        packetBroker,
        keyValueStorage,
        distributedLocks,
        packetPublisher,
        packetCallbackRequester,
        packetSubscriber);
  }

  private static DistributedLocks configureDistributedLocks(
      final HermesConfigurator configurator, final KeyValueStorage keyValueStorage) {
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
    return distributedLocks;
  }

  public EventBusConfig eventBus() {
    return eventBusConfig;
  }

  public HermesConfigurator eventBus(final Consumer<EventBusConfig> mutator) {
    mutator.accept(eventBusConfig);
    return this;
  }

  public PacketBrokerConfig packetBroker() {
    return packetBrokerConfig;
  }

  public HermesConfigurator packetBroker(final Consumer<PacketBrokerConfig> mutator) {
    mutator.accept(packetBrokerConfig);
    return this;
  }

  public PacketCallbackConfig packetCallback() {
    return packetCallbackConfig;
  }

  public HermesConfigurator packetCallback(final Consumer<PacketCallbackConfig> mutator) {
    mutator.accept(packetCallbackConfig);
    return this;
  }

  public PacketCodecConfig packetCodec() {
    return packetCodecConfig;
  }

  public HermesConfigurator packetCodec(final Consumer<PacketCodecConfig> mutator) {
    mutator.accept(packetCodecConfig);
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

    private EventBus eventBus;

    public EventBusConfig() {
      eventBus = EventBusFactory.create().publisher(Runnable::run);
    }

    public EventBus get() {
      return eventBus;
    }

    public void using(final EventBus eventBus) {
      this.eventBus = eventBus;
    }

    public void using(final Consumer<EventBus> mutator) {
      mutator.accept(eventBus);
    }
  }
}
