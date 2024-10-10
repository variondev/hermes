package dev.varion.hermes.message;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface MessageBroker extends Closeable {

  void publish(String channelName, byte[] payload);

  void subscribe(String channelName, MessageListener listener);

  CompletableFuture<byte[]> request(String channelName, byte[] payload);
}
