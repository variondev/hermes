package dev.varion.hermes.packet.codec;

public final class PacketCodecConfig {

  private PacketCodec packetCodec;

  public PacketCodec get() {
    return packetCodec;
  }

  public void using(final PacketCodec packetCodec) {
    this.packetCodec = packetCodec;
  }
}
