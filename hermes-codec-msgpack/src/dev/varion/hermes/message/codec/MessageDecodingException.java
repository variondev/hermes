package dev.varion.hermes.message.codec;

public final class MessageDecodingException extends RuntimeException {

  MessageDecodingException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
