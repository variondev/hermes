package dev.varion.hermes.eventbus.event;

import dev.shiza.dew.event.Event;
import dev.shiza.dew.subscription.Subscriber;
import dev.shiza.dew.subscription.Subscription;
import dev.shiza.dew.subscription.SubscriptionFacade;
import dev.varion.hermes.eventbus.result.ResultHandler;
import dev.varion.hermes.eventbus.result.ResultHandlerFacade;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.Set;

final class EventBusImpl implements EventBus {

  private final SubscriptionFacade subscriptionFacade;
  private final ResultHandlerFacade resultHandlerFacade;
  private EventPublisher eventPublisher;

  EventBusImpl(
          final SubscriptionFacade subscriptionFacade, final ResultHandlerFacade resultHandlerFacade) {
    this.subscriptionFacade = subscriptionFacade;
    this.resultHandlerFacade = resultHandlerFacade;
  }

  @Override
  public EventBus publisher(final EventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
    return this;
  }

  @Override
  public <E extends Event, T> EventBus result(
          final Class<T> resultType, final ResultHandler<E, T> resultHandler) {
    resultHandlerFacade.register(resultType, resultHandler);
    return this;
  }

  @Override
  public void subscribe(final Subscriber subscriber) {
    subscriptionFacade.subscribe(subscriber);
  }

  @Override
  public void publish(
          final EventPublisher eventPublisher, final Event event, final String... targets)
          throws EventPublishingException {
    final Set<Subscription> subscriptions =
            subscriptionFacade.getSubscriptionsByEventType(event.getClass());
    for (final Subscription subscription : subscriptions) {
      notifySubscription(subscription, eventPublisher, event, targets);
    }
  }

  @Override
  public void publish(final Event event, final String... targets) {
    if (eventPublisher == null) {
      throw new EventPublishingException(
              "Could not publish event, because of not specifying default event publisher.");
    }

    publish(eventPublisher, event, targets);
  }

  private void notifySubscription(
          final Subscription subscription,
          final EventPublisher eventPublisher,
          final Event event,
          final String[] targets) {
    final Subscriber subscriber = subscription.subscriber();
    if (hasSpecifiedTarget(targets) && isExcludedSubscription(subscriber, targets)) {
      return;
    }

    for (final MethodHandle invocation : subscription.invocations()) {
      eventPublisher.execute(() -> notifySubscribedMethods(invocation, subscriber, event));
    }
  }

  private void notifySubscribedMethods(
          final MethodHandle invocation, final Subscriber subscriber, final Event event) {
    try {
      final Object returnedValue = invocation.invoke(subscriber, event);
      resultHandlerFacade.process(event, returnedValue);
    } catch (final Throwable exception) {
      throw new EventPublishingException(
              "Could not publish event, because of unexpected exception during method invocation.",
              exception);
    }
  }

  private boolean hasSpecifiedTarget(final String[] targets) {
    return targets.length > 0;
  }

  private boolean isExcludedSubscription(final Subscriber subscriber, final String[] targets) {
    return Arrays.stream(targets).noneMatch(identity -> subscriber.identity().equals(identity));
  }
}
