package dev.varion.hermes;

import static dev.varion.hermes.logger.LoggerFacade.getLoggerFacade;
import static java.lang.Thread.sleep;

import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.message.NatsMessageBroker;
import dev.varion.hermes.serdes.jackson.JacksonPacketSerdes;
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
            .withLoggerFacade(getLoggerFacade(true))
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
      final PongPacket response = new PongPacket(request.getMessage() + " Pong!");
      response.setReplyChannelName(request.getReplyChannelName());
      return response;
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}
