package dev.varion.hermes.message.codec;

public final class MessageCodecConfig {

  private MessageCodec messageCodec;

  public MessageCodec get() {
    return messageCodec;
  }

  public void using(final MessageCodec messageCodec) {
    this.messageCodec = messageCodec;
  }
}
