package dev.varion.hermes;

public final class HermesTests {

  public static void main(final String[] args) {
    Hermes.newBuilder().withMessageBroker(N).build();
  }
}
