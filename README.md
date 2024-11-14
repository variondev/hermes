# **Hermes Framework**

**Hermes** is a high-performance, modular framework tailored for advanced packet handling and asynchronous communication in distributed systems. It supports `publish-subscribe` messaging, asynchronous `request-response` operations using `CompletableFuture`, robust `key-value` storage, and `distributed locking` mechanisms—all with advanced error management to ensure system reliability.

## **Quick Start Guide**

### **Repository Setup**

To add Hermes to your project, include the following Maven repository:

```kotlin
maven("https://repo.varion.dev/releases")
```

### **Add Dependencies**

#### Core Components

```kotlin
implementation("dev.varion.hermes:hermes-common:1.1.2")
```

#### Codec Support

To handle serialization and deserialization, include:

```kotlin
// jackson
implementation("dev.varion.hermes:hermes-codec-jackson:1.1.2")
implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
// msgpack + jackson
implementation("dev.varion.hermes:hermes-codec-msgpack-jackson:1.1.2")
implementation("org.msgpack:jackson-dataformat-msgpack:0.9.8")
```

#### Bridge Integrations

For bridge-based communication:

```kotlin
// nats
implementation("dev.varion.hermes:hermes-bridge-nats:1.1.2")
implementation("io.nats:jnats:2.20.2")
// redis
implementation("dev.varion.hermes:hermes-bridge-redis:1.1.2")
implementation("io.lettuce:lettuce-core:6.4.0.RELEASE")
```

[**Examples of Hermes Implementations**](hermes-common/test)

---

## **Hermes Configuration Example**

Below is a sample configuration of Hermes with Redis-based packet brokering and key-value storage. This setup also integrates Jackson-based message codecs for structured packet handling.

```java
final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
final ObjectMapper msgpackMapper = MsgpackJacksonObjectMapperFactory.getMsgpackJacksonObjectMapper();

final Hermes hermes = HermesConfigurator.configure(configurator ->
    configurator
        .packetBroker(config -> config.using(RedisPacketBroker.create(redisClient)))
        .keyValue(config -> config.using(RedisKeyValueStorage.create(redisClient)))
        .distributedLock(config -> config.using(true))
        .packetCallback(config -> config.requestCleanupInterval(Duration.ofSeconds(10L)))
        .packetCodec(config -> config.using(JacksonPacketCodecFactory.getJacksonPacketCodec(msgpackMapper))));

// Ensure to close the Hermes instance during shutdown
hermes.close();
```

### **Example Packet Structure**

Hermes supports flexible packet structures, adaptable for P2P, C2S, or S2C models. Here is a basic example of a request packet.

```java
public class ExampleRequestPacket extends JacksonPacket {

  private String content;

  public ExampleRequestPacket() { }

  public ExampleRequestPacket(final String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
```

### **Subscriber Example**

Hermes allows defining subscribers with flexible receiving logic. Subscribers are isolated to specific topics but can be scaled across multiple instances as needed.

```java
public class ExampleListener implements Subscriber {

  @Subscribe
  public Packet receive(final ExampleRequestPacket request) {
    if (condition) {
      return null; // Return null if conditions are not met
    }
    final ExampleResponsePacket response = new ExampleResponsePacket(request.getContent() + " Pong!");
    return response.dispatchTo(request.getUniqueId());
  }

  @Subscribe
  public void receive(final BroadcastPacket packet) {
    System.out.printf("Received P2P packet: %s%n", packet.getContent());
  }

  @Override
  public String identity() {
    return "tests";
  }
}
```

### **Distributed Locking Example**

Below is an example demonstrating the use of Hermes' distributed locking, ideal for synchronizing access to shared resources in multi-threaded environments.

```java
final DistributedLock lock = hermes.distributedLocks().createLock("my_lock");
lock.execute(() -> {
      System.out.println("Thread " + i + " acquired the lock!");
      try { Thread.sleep(100); } catch (InterruptedException ignored) {}
      },
      Duration.ofMillis(10L),
      Duration.ofSeconds(5L))
    .whenComplete((unused, throwable) -> System.out.println("Thread " + i + " released the lock!"))
    .join();
```

---

<p align="center">
  <img height="100em" src="https://count.getloli.com/get/@:awa?theme=rule33" alt="usage-count"/>
</p>