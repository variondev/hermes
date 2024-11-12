package dev.varion.hermes.packet;

public final class PacketBrokerConfig {

  private PacketBroker packetBroker;

  public PacketBroker get() {
    return packetBroker;
  }

  public void using(final PacketBroker packetBroker) {
    this.packetBroker = packetBroker;
  }
}
