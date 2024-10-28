package dev.araucaris.hermes;

import static java.lang.Thread.sleep;

import dev.araucaris.hermes.message.Message;
import dev.araucaris.hermes.message.RedisMessageBroker;
import dev.araucaris.hermes.message.codec.MessagePackCodec;
import dev.shiza.dew.subscription.Subscribe;
import dev.shiza.dew.subscription.Subscriber;
import io.lettuce.core.RedisClient;

public final class PongServer {

  private PongServer() {}

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  public static void main(final String[] args) {
    try (final Hermes hermes =
        HermesConfigurator.configure(
            configurator -> {
              configurator.messageBroker(
                  config ->
                      config.using(
                          RedisMessageBroker.create(RedisClient.create("redis://localhost:6379"))));
              configurator.messageCodec(config -> config.using(MessagePackCodec.create()));
            })) {

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
      System.out.printf("Received peer message: %s%n", packet.getContent());
    }

    @Override
    public String identity() {
      return "tests";
    }
  }
}
