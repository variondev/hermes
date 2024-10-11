package dev.varion.hermes;

import static java.util.logging.Level.FINER;

import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketPublisher;
import dev.varion.hermes.packet.PacketRequester;
import dev.varion.hermes.packet.PacketSubscriber;
import dev.varion.hermes.packet.callback.PacketCallbackRequest;
import dev.varion.hermes.packet.callback.PacketCallbackResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class HermesImpl implements Hermes {

  private final LoggerFacade loggerFacade;
  private final MessageBroker messageBroker;
  private final PacketPublisher packetPublisher;
  private final PacketRequester packetRequester;
  private final PacketSubscriber packetSubscriber;

  HermesImpl(
      final LoggerFacade loggerFacade,
      final MessageBroker messageBroker,
      final PacketPublisher packetPublisher,
      final PacketRequester packetRequester,
      final PacketSubscriber packetSubscriber) {
    this.loggerFacade = loggerFacade;
    this.messageBroker = messageBroker;
    this.packetPublisher = packetPublisher;
    this.packetRequester = packetRequester;
    this.packetSubscriber = packetSubscriber;
  }

  @Override
  public <T extends Packet> void publish(final String channelName, final T packet) {
    packetPublisher.publish(channelName, packet);
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    loggerFacade.log(
        FINER,
        "Channel %s is now being observed by %s listener for incoming packets.",
        subscriber.identity(),
        subscriber.getClass().getSimpleName());
    packetSubscriber.subscribe(subscriber);
  }

  @Override
  public <T extends Packet & PacketCallbackResponse, R extends Packet & PacketCallbackRequest>
      CompletableFuture<T> request(final String channelName, final R request) {
    return packetRequester.request(channelName, request);
  }

  @Override
  public void close() throws IOException {
    messageBroker.close();
  }
}
