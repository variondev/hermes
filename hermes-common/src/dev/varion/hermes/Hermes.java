package dev.varion.hermes;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.kv.KeyValueStorage;
import dev.varion.hermes.locks.DistributedLocks;
import dev.varion.hermes.packet.Packet;
import java.io.Closeable;
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
}
