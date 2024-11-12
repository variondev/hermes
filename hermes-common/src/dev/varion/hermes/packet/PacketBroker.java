package dev.varion.hermes.packet;

import dev.varion.hermes.HermesListener;
import java.io.Closeable;
import java.util.concurrent.CompletionStage;

public interface PacketBroker extends Closeable {

  CompletionStage<Long> publish(String channelName, byte[] payload);

  void subscribe(String channelName, HermesListener listener);
}
