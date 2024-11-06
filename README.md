# Hermes

**Hermes** is a versatile framework for advanced message handling, providing `publish-subscribe`
mechanisms, asynchronous `request-response` capabilities with CompletableFuture, access to
`key-value` storage, and `distributed locking`, all with robust exception handling.

## Getting Started

### Add Repository

```kotlin
maven("https://repo.varion.dev/releases")
```

### Add Dependencies

**Core:**

```kotlin
implementation("dev.varion.hermes:hermes-common:1.0")
```

**Codec:**

```kotlin
implementation("dev.varion.hermes:hermes-codec-jackson:1.0")
implementation("dev.varion.hermes:hermes-codec-msgpack:1.0")
```

**Bridge:**

```kotlin
implementation("dev.varion.hermes:hermes-bridge-nats:1.0")
implementation("dev.varion.hermes:hermes-bridge-redis:1.0")
```

---

<p align="center">
  <img height="100em" src="https://count.getloli.com/get/@:awa?theme=rule33" alt="loli"/>
</p>
