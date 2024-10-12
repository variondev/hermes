package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.distributed.DistributedLocks;
import dev.varion.hermes.keyvalue.KeyValueStorage;
import dev.varion.hermes.message.Message;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface Hermes extends Closeable {

  <T extends Message> void publish(String channelName, T packet);

  void subscribe(Subscriber subscriber);

  <T extends Message> CompletableFuture<T> request(String channelName, Message request);

  KeyValueStorage keyValue() throws MissingServiceException;

  DistributedLocks distributedLocks() throws MissingServiceException;
}
