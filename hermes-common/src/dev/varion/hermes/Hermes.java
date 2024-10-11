package dev.varion.hermes;

import dev.shiza.dew.event.EventBus;
import dev.shiza.dew.subscription.Subscriber;
import dev.varion.hermes.logger.LoggerFacade;
import dev.varion.hermes.message.MessageBroker;
import dev.varion.hermes.packet.Packet;
import dev.varion.hermes.packet.PacketProcessor;
import dev.varion.hermes.packet.PacketPublisher;
import dev.varion.hermes.packet.PacketRequester;
import dev.varion.hermes.packet.PacketSubscriber;
import dev.varion.hermes.packet.serdes.PacketSerdes;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface Hermes {

  static HermesBuilder newBuilder() {
    return new HermesBuilder();
  }

  <T extends Packet> void publish(String channelName, T packet);

  void subscribe(Subscriber subscriber);

  <T extends Packet, R extends Packet> CompletableFuture<R> request(String channelName, T packet);

  void close() throws IOException;

  final class HermesBuilder {

    private LoggerFacade loggerFacade = LoggerFacade.create(false);
    private MessageBroker messageBroker;
    private PacketSerdes packetSerdes;
    private EventBus eventBus = EventBus.create().publisher(Runnable::run);

    HermesBuilder() {}

    public HermesBuilder withLoggerFacade(final LoggerFacade loggerFacade) {
      this.loggerFacade = loggerFacade;
      return this;
    }

    public HermesBuilder withMessageBroker(final MessageBroker messageBroker) {
      this.messageBroker = messageBroker;
      return this;
    }

    public HermesBuilder withPacketSerdes(final PacketSerdes packetSerdes) {
      this.packetSerdes = packetSerdes;
      return this;
    }

    public HermesBuilder withEventBus(final EventBus eventBus) {
      this.eventBus = eventBus;
      return this;
    }

    public Hermes build() throws HermesException {
      if (messageBroker == null) {
        throw new HermesException("Missing message broker.");
      }

      if (packetSerdes == null) {
        throw new HermesException("Missing packet serdes.");
      }

      final PacketProcessor packetProcessor = PacketProcessor.create(loggerFacade, packetSerdes);
      final PacketPublisher packetPublisher =
          PacketPublisher.create(loggerFacade, messageBroker, packetSerdes);
      final PacketRequester packetRequester =
          PacketRequester.create(loggerFacade, messageBroker, packetProcessor, packetSerdes);
      final PacketSubscriber packetSubscriber =
          PacketSubscriber.create(
              eventBus,
              loggerFacade,
              messageBroker,
              packetPublisher,
              packetProcessor,
              packetSerdes);
      return new HermesImpl(
          loggerFacade, messageBroker, packetPublisher, packetRequester, packetSubscriber);
    }
  }
}
