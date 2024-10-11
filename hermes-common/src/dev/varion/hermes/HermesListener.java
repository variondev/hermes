package dev.varion.hermes;

@FunctionalInterface
public interface HermesListener {

  void receive(String channelName, byte[] payload);
}
