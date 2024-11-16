package dev.varion.hermes;

import dev.varion.hermes.packet.codec.JacksonPacket;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MasterSlaveResponsePacket extends JacksonPacket {

  private int port;
  private int memoryUsagePercent;
  private double tps;
  private String address;
  private ArrayList<ExampleEnum> serverCategory;
  private Map<UUID, String> players;
  private Map<UUID, ExampleEnum> queuePlayers;
  private long bootTime;
  private int maxPlayers;

  public MasterSlaveResponsePacket(
      final Map<UUID, ExampleEnum> queuePlayers,
      final int port,
      final int memoryUsagePercent,
      final double tps,
      final String address,
      final ArrayList<ExampleEnum> serverCategory,
      final Map<UUID, String> players,
      final long bootTime,
      final int maxPlayers) {
    this.queuePlayers = queuePlayers;
    this.port = port;
    this.memoryUsagePercent = memoryUsagePercent;
    this.tps = tps;
    this.address = address;
    this.serverCategory = serverCategory;
    this.players = players;
    this.bootTime = bootTime;
    this.maxPlayers = maxPlayers;
  }

  public MasterSlaveResponsePacket() {}

  public int getPort() {
    return port;
  }

  public int getMemoryUsagePercent() {
    return memoryUsagePercent;
  }

  public double getTps() {
    return tps;
  }

  public String getAddress() {
    return address;
  }

  public ArrayList<ExampleEnum> getServerCategory() {
    return serverCategory;
  }

  public Map<UUID, String> getPlayers() {
    return players;
  }

  public Map<UUID, ExampleEnum> getQueuePlayers() {
    return queuePlayers;
  }

  public long getBootTime() {
    return bootTime;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public enum ExampleEnum {
    ONE("dsa"),
    TWO("dsa"),
    THREE("dsa");

    private final String xd;

    ExampleEnum(final String xd) {
      this.xd = xd;
    }

    public String getXd() {
      return xd;
    }
  }
}
