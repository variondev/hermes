package dev.varion.hermes;

import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.Message;
import dev.varion.hermes.message.RedisMessageBroker;
import dev.varion.hermes.message.codec.MessagePackCodec;
import io.lettuce.core.RedisClient;

public final class PongServer {

  private PongServer() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(
                RedisMessageBroker.create(RedisClient.create("redis://localhost:6379")))
            .withPacketSerdes(MessagePackCodec.create())
            .build()) {

      hermes.subscribe(new PongListener());

      // keep-alive
      while (true) {
        sleep(1000);
      }
    } catch (final Exception exception) {
      exception.printStackTrace();
    }
  }

  public static final class PongListener implements Subscriber {

    @Subscribe
    public Message receive(final PingMessage request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      final PongMessage response = new PongMessage(request.getContent() + " Pong!");
      return response.dispatchTo(request.getUniqueId());
    }

    @Subscribe
    public void receive(final PeerMessage packet) {
      System.out.printf("Received peer packet: %s%n", packet.getContent());
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}
