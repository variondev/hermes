package dev.varion.hermes.eventbus.result;

import dev.shiza.dew.event.Event;

public interface ResultHandlerFacade {

  <E extends Event, T> void register(final Class<T> resultType, final ResultHandler<E, T> resultHandler);

  <E extends Event, T> void process(final E event, final T value);
}
