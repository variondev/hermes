package dev.varion.hermes.callback;

import static java.time.Duration.ofSeconds;

import java.time.Duration;

public final class PacketCallbackConfig {

  private Duration requestCleanupInterval;

  public PacketCallbackConfig() {
    requestCleanupInterval = ofSeconds(10L);
  }

  public Duration requestCleanupInterval() {
    return requestCleanupInterval;
  }

  public void requestCleanupInterval(final Duration requestCleanupInterval) {
    this.requestCleanupInterval = requestCleanupInterval;
  }
}
