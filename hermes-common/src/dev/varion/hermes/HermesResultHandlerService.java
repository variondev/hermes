package dev.varion.hermes;

import dev.shiza.dew.result.ResultHandler;
import dev.shiza.dew.result.ResultHandlerFacade;
import dev.shiza.dew.result.ResultHandlingException;
import java.util.HashMap;
import java.util.Map;

final class HermesResultHandlerService implements ResultHandlerFacade {

  private final Map<Class<?>, ResultHandler<?>> handlers;

  HermesResultHandlerService(final Map<Class<?>, ResultHandler<?>> handlers) {
    this.handlers = handlers;
  }

  public static ResultHandlerFacade create() {
    return new HermesResultHandlerService(new HashMap<>());
  }

  @Override
  public <T> void register(final Class<T> resultType, final ResultHandler<T> resultHandler) {
    handlers.put(resultType, resultHandler);
  }

  @Override
  public <T> void process(final T value) {
    if (value == null) {
      return;
    }

    for (final Map.Entry<Class<?>, ResultHandler<?>> entry : handlers.entrySet()) {
      if (entry.getKey().isAssignableFrom(value.getClass())
          || value.getClass().isAssignableFrom(entry.getKey())) {
        //noinspection unchecked
        ((ResultHandler<T>) entry.getValue()).handle(value);
        return;
      }
    }

    throw new ResultHandlingException(
        "Could not handle result of type %s, because of missing result handler."
            .formatted(value.getClass().getName()));
  }
}
