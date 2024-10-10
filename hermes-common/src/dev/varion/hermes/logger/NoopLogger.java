package dev.varion.hermes.logger;

import java.util.logging.Level;

final class NoopLogger implements LoggerFacade {

  @Override
  public void log(final Level level, final String message, final Object... parameters) {
    /* No operation logger */
  }
}
