package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import java.io.Closeable;
import java.util.concurrent.CompletionStage;

public interface MessageBroker extends Closeable {

  CompletionStage<Long> publish(String channelName, byte[] payload);

  void subscribe(String channelName, HermesListener listener);
}
