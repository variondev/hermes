package dev.araucaris.hermes;

import dev.araucaris.hermes.distributed.DistributedLocks;
import dev.araucaris.hermes.keyvalue.KeyValueStorage;
import dev.araucaris.hermes.message.Message;
import dev.shiza.dew.subscription.Subscriber;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface Hermes extends Closeable {

  <T extends Message> void publish(String channelName, T packet);

  void subscribe(Subscriber subscriber);

  <T extends Message> CompletableFuture<T> request(String channelName, Message request);

  KeyValueStorage keyValue() throws MissingServiceException;

  DistributedLocks distributedLocks() throws MissingServiceException;
}
