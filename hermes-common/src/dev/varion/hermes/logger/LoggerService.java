package dev.varion.hermes.logger;

import static java.util.logging.Level.ALL;
import static java.util.logging.Logger.getLogger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

final class LoggerService implements LoggerFacade {

  private final boolean debug;
  private final Logger logger;

  LoggerService(final boolean debug) {
    this.debug = debug;
    logger = getConfiguredLogger();
  }

  @Override
  public void log(final Level level, final String message, final Object... parameters) {
    if (!debug) {
      return;
    }

    logger.log(level, message.formatted(parameters));
  }

  private Logger getConfiguredLogger() {
    final Logger underlyingLogger = getLogger(getClass().getSimpleName());
    for (final Handler handler : underlyingLogger.getHandlers()) {
      underlyingLogger.removeHandler(handler);
    }

    final Handler handler = new ConsoleHandler();
    handler.setLevel(ALL);

    underlyingLogger.addHandler(handler);
    underlyingLogger.setLevel(ALL);
    return underlyingLogger;
  }
}
