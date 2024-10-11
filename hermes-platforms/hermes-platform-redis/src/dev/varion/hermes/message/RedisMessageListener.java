package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import dev.varion.hermes.packet.serdes.PacketSerdesException;
import io.lettuce.core.pubsub.RedisPubSubListener;

final class RedisMessageListener implements RedisPubSubListener<String, byte[]> {

  private final PacketSerdes packetSerdes;
  private final String subscribedTopic;
  private final HermesListener listener;

  RedisMessageListener(
      final PacketSerdes packetSerdes,
      final String subscribedTopic,
      final HermesListener listener) {
    this.packetSerdes = packetSerdes;
    this.subscribedTopic = subscribedTopic;
    this.listener = listener;
  }

  @Override
  public void message(final String channelName, final byte[] message) {
    final boolean whetherIsSubscribedTopic = subscribedTopic.equals(channelName);
    if (whetherIsSubscribedTopic) {
      try {
        listener.receive(channelName, packetSerdes.deserialize(message));
      } catch (final Exception exception) {
        throw new PacketSerdesException(
            "Could not process process incoming message, because of unexpected exception.",
            exception);
      }
    }
  }

  @Override
  public void message(final String pattern, final String channelName, final byte[] message) {
    message("%s:%s".formatted(pattern, channelName), message);
  }

  @Override
  public void subscribed(final String channel, final long count) {}

  @Override
  public void psubscribed(final String pattern, final long count) {}

  @Override
  public void unsubscribed(final String channel, final long count) {}

  @Override
  public void punsubscribed(final String pattern, final long count) {}
}
