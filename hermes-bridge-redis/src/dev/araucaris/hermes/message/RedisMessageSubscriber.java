package dev.araucaris.hermes.message;

import dev.araucaris.hermes.HermesListener;
import io.lettuce.core.pubsub.RedisPubSubListener;

final class RedisMessageSubscriber implements RedisPubSubListener<String, byte[]> {

  private final String subscribedTopic;
  private final HermesListener listener;

  RedisMessageSubscriber(final String subscribedTopic, final HermesListener listener) {
    this.subscribedTopic = subscribedTopic;
    this.listener = listener;
  }

  @Override
  public void message(final String channelName, final byte[] message) {
    final boolean whetherIsSubscribedTopic = subscribedTopic.equals(channelName);
    if (whetherIsSubscribedTopic) {
      listener.receive(channelName, message);
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
