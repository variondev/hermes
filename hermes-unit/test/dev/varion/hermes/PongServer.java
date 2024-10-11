package dev.varion.hermes;

import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.NatsMessageBroker;
import dev.varion.hermes.packet.serdes.jackson.JacksonPacketSerdes;
import io.nats.client.Nats;
import java.io.IOException;

public final class PongServer {

  private PongServer() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) throws IOException, InterruptedException {
    final Hermes hermes =
        Hermes.newBuilder()
            .withMessageBroker(NatsMessageBroker.create(Nats.connect()))
            .withPacketSerdes(JacksonPacketSerdes.create())
            .build();

    hermes.subscribe(new PongListener());

    // keep-alive
    while (true) {
      sleep(1000);
    }
  }

  public static final class PongListener implements Subscriber {

    @Subscribe
    public PongPacket receive(final PingPacket request) {
      // method can be a void, no need to return any packets,
      // if response cannot be sent it's also
      // fine you can return null
      final PongPacket response = new PongPacket(request.getMessage() + " Pong!");
      response.setUniqueId(request.getUniqueId());
      return response;
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
