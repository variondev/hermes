package dev.varion.hermes.logger;

import java.util.logging.Level;

@FunctionalInterface
public interface LoggerFacade {

  static LoggerFacade getHermesLogger(final boolean debug) {
    return new HermesLogger(debug);
  }

  static LoggerFacade getNoopLogger() {
    return new NoopLogger();
  }

  void log(final Level level, final String message, final Object... parameters);
}
