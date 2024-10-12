package dev.varion.hermes;

import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.RedisMessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.serdes.jackson.JacksonPacketSerdes;
import io.lettuce.core.RedisClient;

public final class PongServer {

  private PongServer() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {
    try (final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(RedisMessageBroker.create(RedisClient.create()))
            .withPacketSerdes(JacksonPacketSerdes.create())
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
    public Packet receive(final PingPacket request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      final PongPacket response = new PongPacket(request.getMessage() + " Pong!");
      return response.sendTo(request.getUniqueId());
    }

    @Subscribe
    public void receive(final PeerPacket packet) {
      System.out.printf("Received peer packet: %s%n", packet.getContent());
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}
