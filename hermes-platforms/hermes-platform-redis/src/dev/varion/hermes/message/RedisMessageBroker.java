package dev.varion.hermes.message;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.util.HashSet;
import java.util.Set;

public final class RedisMessageBroker implements MessageBroker {

  private static final RedisCodec<String, byte[]> DEFAULT_CODEC = new RedisBinaryCodec();
  private final PacketSerdes packetSerdes;
  private final RedisClient redisClient;
  private final StatefulRedisConnection<String, byte[]> connection;
  private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;
  private final Set<String> subscribedTopics;

  RedisMessageBroker(final PacketSerdes packetSerdes, final RedisURI redisUri) {
    this.packetSerdes = packetSerdes;
    redisClient = RedisClient.create(redisUri);
    connection = redisClient.connect(DEFAULT_CODEC);
    pubSubConnection = redisClient.connectPubSub(DEFAULT_CODEC);
    subscribedTopics = new HashSet<>();
  }

  public static MessageBroker create(final PacketSerdes serdesContext, final RedisURI redisUri) {
    return new RedisMessageBroker(serdesContext, redisUri);
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
    pubSubConnection.addListener(new RedisMessageListener(channelName, listener));
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
