package dev.varion.hermes.callback;

import static java.time.Duration.ofSeconds;

import java.time.Duration;

public final class PacketCallbackConfig {

  private PacketCallbackFacade packetCallbackFacade;
  private Duration requestCleanupInterval;

  public PacketCallbackConfig() {
    packetCallbackFacade = PacketCallbackFacade.create();
    requestCleanupInterval = ofSeconds(10L);
  }

  public Duration requestCleanupInterval() {
    return requestCleanupInterval;
  }

  public void requestCleanupInterval(final Duration requestCleanupInterval) {
    this.requestCleanupInterval = requestCleanupInterval;
  }

  public PacketCallbackFacade packetCallbackFacade() {
    return packetCallbackFacade;
  }

  public void packetCallbackFacade(final PacketCallbackFacade packetCallbackFacade) {
    this.packetCallbackFacade = packetCallbackFacade;
  }
}
