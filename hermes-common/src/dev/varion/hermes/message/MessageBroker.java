package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface MessageBroker extends Closeable {

  void publish(String channelName, byte[] payload);

  void subscribe(String channelName, HermesListener listener);

  CompletableFuture<byte[]> request(String channelName, byte[] payload);
}
