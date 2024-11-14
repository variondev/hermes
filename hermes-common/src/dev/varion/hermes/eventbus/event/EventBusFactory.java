package dev.varion.hermes.eventbus.event;

import dev.shiza.dew.subscription.SubscriptionFacade;
import dev.varion.hermes.eventbus.result.ResultHandlerFacade;

public final class EventBusFactory {

  private EventBusFactory() {}

  public static EventBus create(
      final SubscriptionFacade subscriptionFacade, final ResultHandlerFacade resultHandlerFacade) {
    return new EventBusImpl(subscriptionFacade, resultHandlerFacade);
  }

}
