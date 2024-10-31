package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.message.callback.requester.MessageCallbackRequester;
import dev.varion.hermes.message.pubsub.MessagePublisher;
import dev.varion.hermes.message.pubsub.MessageSubscriber;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class HermesImpl implements Hermes {

  private final MessageBroker messageBroker;
  private final KeyValueStorage keyValueStorage;
  private final DistributedLocks distributedLocks;
  private final MessagePublisher messagePublisher;
  private final MessageCallbackRequester messageCallbackRequester;
  private final MessageSubscriber messageSubscriber;

  HermesImpl(
      final MessageBroker messageBroker,
      final KeyValueStorage keyValueStorage,
      final DistributedLocks distributedLocks,
      final MessagePublisher messagePublisher,
      final MessageCallbackRequester messageCallbackRequester,
      final MessageSubscriber messageSubscriber) {
    this.messageBroker = messageBroker;
    this.keyValueStorage = keyValueStorage;
    this.distributedLocks = distributedLocks;
    this.messagePublisher = messagePublisher;
    this.messageCallbackRequester = messageCallbackRequester;
    this.messageSubscriber = messageSubscriber;
  }

  @Override
  public <T extends Message> void publish(final String channelName, final T packet) {
    messagePublisher.publish(channelName, packet);
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    messageSubscriber.subscribe(subscriber);
  }

  @Override
  public <T extends Message> CompletableFuture<T> request(
      final String channelName, final Message request) {
    return messageCallbackRequester.request(channelName, request);
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
    messageBroker.close();
  }
}
