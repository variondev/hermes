package dev.varion.hermes;

@FunctionalInterface
public interface HermesListener {

  void receive(String replyChannelName, byte[] payload);
}
