package dev.varion.hermes.packet;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.codec.RedisBinaryCodec;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public final class RedisPacketBroker implements PacketBroker {

  private final RedisClient redisClient;
  private final StatefulRedisConnection<String, byte[]> connection;
  private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;
  private final Set<String> subscribedTopics;

  RedisPacketBroker(final RedisClient redisClient) {
    this.redisClient = redisClient;
    final RedisBinaryCodec codec = RedisBinaryCodec.INSTANCE;
    connection = redisClient.connect(codec);
    pubSubConnection = redisClient.connectPubSub(codec);
    subscribedTopics = new HashSet<>();
  }

  public static PacketBroker create(final RedisClient redisClient) {
    return new RedisPacketBroker(redisClient);
  }

  @Override
  public CompletionStage<Long> publish(final String channelName, final byte[] payload) {
    return connection
        .async()
        .publish(channelName, payload)
        .exceptionally(
            cause -> {
              throw new PacketBrokerException(
                  "Could not publish a message, because of unexpected exception.", cause);
            });
  }

  @Override
  public void subscribe(final String channelName, final HermesListener listener) {
    pubSubConnection.addListener(new RedisPacketSubscriber(channelName, listener));
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
