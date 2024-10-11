package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import java.io.Closeable;

public interface MessageBroker extends Closeable {

  void publish(String channelName, byte[] payload);

  void subscribe(String channelName, HermesListener listener);
}
