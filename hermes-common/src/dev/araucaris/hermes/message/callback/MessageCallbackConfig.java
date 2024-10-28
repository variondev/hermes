package dev.araucaris.hermes.message.callback;

import static java.time.Duration.ofSeconds;

import java.time.Duration;

public final class MessageCallbackConfig {

  private Duration requestCleanupInterval;

  public MessageCallbackConfig() {
    requestCleanupInterval = ofSeconds(10L);
  }

  public Duration requestCleanupInterval() {
    return requestCleanupInterval;
  }

  public void requestCleanupInterval(final Duration requestCleanupInterval) {
    this.requestCleanupInterval = requestCleanupInterval;
  }
}
