package dev.varion.hermes.logger;

import static java.util.logging.Logger.getLogger;

import dev.varion.hermes.Hermes;
import java.util.logging.Level;
import java.util.logging.Logger;

final class LoggerService implements LoggerFacade {

  private final boolean debug;
  private final Logger logger;

  LoggerService(final boolean debug) {
    this.debug = debug;
    logger = getLogger(Hermes.class.getName());
  }

  @Override
  public void log(final Level level, final String message, final Object... parameters) {
    if (!debug) {
      return;
    }

    logger.log(level, message.formatted(parameters));
  }
}
