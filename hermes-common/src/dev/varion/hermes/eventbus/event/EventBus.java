package dev.varion.hermes.eventbus.event;

import dev.shiza.dew.event.Event;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.SubscribingException;
import dev.varion.hermes.eventbus.result.ResultHandler;
import org.jetbrains.annotations.Contract;

public sealed interface EventBus permits EventBusImpl {

  @Contract("_ -> this")
  EventBus publisher(final EventPublisher eventPublisher);

  @Contract("_, _ -> this")
  <E extends Event, T> EventBus result(
          final Class<T> resultType, final ResultHandler<E, T> resultHandler);

  void subscribe(final Subscriber subscriber) throws SubscribingException;

  void publish(final EventPublisher eventPublisher, final Event event, final String... targets)
          throws EventPublishingException;

  void publish(final Event event, final String... targets) throws EventPublishingException;
}
