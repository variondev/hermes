package dev.varion.hermes.eventbus.result;

public interface ResultHandler<E, T> {

  void handle(final E event, final T result);
}
