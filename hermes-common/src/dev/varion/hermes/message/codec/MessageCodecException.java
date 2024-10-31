package dev.varion.hermes.message.codec;

public final class MessageCodecException extends RuntimeException {

  MessageCodecException(final String message) {
    super(message);
  }

  MessageCodecException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
