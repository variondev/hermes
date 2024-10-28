package dev.araucaris.hermes.keyvalue;

public final class KeyValueConfig {

  private KeyValueStorage keyValueStorage;

  public KeyValueStorage get() {
    return keyValueStorage;
  }

  public void using(final KeyValueStorage keyValueStorage) {
    this.keyValueStorage = keyValueStorage;
  }
}
