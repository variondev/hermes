package dev.varion.hermes.eventbus.event;

@FunctionalInterface
public interface EventPublisher {

  void execute(final Runnable task);
}
