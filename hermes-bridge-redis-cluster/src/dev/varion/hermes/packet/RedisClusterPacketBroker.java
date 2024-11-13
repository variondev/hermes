package dev.varion.hermes.packet;

import dev.varion.hermes.HermesListener;
import dev.varion.hermes.packet.codec.RedisBinaryCodec;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public final class RedisClusterPacketBroker implements PacketBroker {

  private final RedisClusterClient redisClusterClient;
  private final StatefulRedisClusterConnection<String, byte[]> connection;
  private final StatefulRedisClusterPubSubConnection<String, byte[]> pubSubConnection;
  private final Set<String> subscribedTopics;

  RedisClusterPacketBroker(final RedisClusterClient redisClusterClient) {
    this.redisClusterClient = redisClusterClient;
    final RedisBinaryCodec codec = RedisBinaryCodec.INSTANCE;
    connection = redisClusterClient.connect(codec);
    pubSubConnection = redisClusterClient.connectPubSub(codec);
    subscribedTopics = new HashSet<>();
  }

  public static PacketBroker create(final RedisClusterClient redisClusterClient) {
    return new RedisClusterPacketBroker(redisClusterClient);
  }

  public static PacketBroker create(final Iterable<RedisURI> redisURIs) {
    return new RedisClusterPacketBroker(RedisClusterClient.create(redisURIs));
  }

  @Override
  public CompletionStage<Long> publish(final String channelName, final byte[] payload) {
    return connection
        .async()
        .publish(channelName, payload)
        .exceptionally(
            cause -> {
              throw new PacketBrokerException(
                  "Could not publish a packet, because of unexpected exception.", cause);
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
    redisClusterClient.close();
    connection.close();
    pubSubConnection.close();
    subscribedTopics.clear();
  }
}
