package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.message.codec.RedisBinaryCodec;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.util.HashSet;
import java.util.Set;

public final class RedisMessageBroker implements MessageBroker {

  private final RedisClient redisClient;
  private final StatefulRedisConnection<String, byte[]> connection;
  private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;
  private final Set<String> subscribedTopics;

  RedisMessageBroker(final RedisClient redisClient) {
    this.redisClient = redisClient;
    final RedisBinaryCodec codec = RedisBinaryCodec.INSTANCE;
    connection = redisClient.connect(codec);
    pubSubConnection = redisClient.connectPubSub(codec);
    subscribedTopics = new HashSet<>();
  }

  public static MessageBroker create(final RedisClient redisClient) {
    return new RedisMessageBroker(redisClient);
  }

  public static MessageBroker create(final RedisURI redisUri) {
    return create(RedisClient.create(redisUri));
  }

  @Override
  public void publish(final String channelName, final byte[] payload) {
    connection
        .async()
        .publish(channelName, payload)
        .exceptionally(
            cause -> {
              throw new MessageBrokerException(
                  "Could not publish a message, because of unexpected exception.", cause);
            });
  }

  @Override
  public void subscribe(final String channelName, final HermesListener listener) {
    pubSubConnection.addListener(new RedisMessageSubscriber(channelName, listener));
    if (subscribedTopics.contains(channelName)) {
      return;
    }
    subscribedTopics.add(channelName);
    pubSubConnection.sync().subscribe(channelName);
  }

  @Override
  public void close() {
    redisClient.close();
    connection.close();
    pubSubConnection.close();
    subscribedTopics.clear();
  }
}
