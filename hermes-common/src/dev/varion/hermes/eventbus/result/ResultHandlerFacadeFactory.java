package dev.varion.hermes.eventbus.result;

import java.util.HashMap;

public final class ResultHandlerFacadeFactory {

  private ResultHandlerFacadeFactory() {}

  public static ResultHandlerFacade create() {
    return new ResultHandlerService(new HashMap<>());
  }
}
