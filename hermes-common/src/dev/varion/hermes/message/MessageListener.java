package dev.varion.hermes.message;

@FunctionalInterface
public interface MessageListener {

  void receive(String channelName, String replyChannelName, byte[] payload);
}
