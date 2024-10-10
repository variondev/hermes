package dev.varion.hermes.logger;

import java.util.logging.Level;

@FunctionalInterface
public interface LoggerFacade {

  static LoggerFacade create(final boolean debug) {
    return new LoggerService(debug);
  }

  void log(final Level level, final String message, final Object... parameters);
}
