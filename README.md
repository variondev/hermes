# Hermes

Welcome to **Hermes**, the easy-to-use, flexible, and scalable messaging library designed to make
sending binary data effortless. Whether you're working with popular message brokers or building a
custom solution, Hermes has you covered.

## Getting Started

### Add Repository

```kotlin
maven("https://repo.varion.dev/snapshots")
```

### Add Dependencies

**Core:**

```kotlin
implementation("dev.varion:hermes-common:1.0-SNAPSHOT")
```

**Codec:**

```kotlin
implementation("dev.varion:hermes-codec-jackson:1.0-SNAPSHOT")
implementation("dev.varion:hermes-codec-msgpack:1.0-SNAPSHOT")
```

**Bridge:**

```kotlin
implementation("dev.varion:hermes-bridge-nats:1.0-SNAPSHOT")
implementation("dev.varion:hermes-bridge-redis:1.0-SNAPSHOT")
```

---

<p align="center">
  <img height="100em" src="https://count.getloli.com/get/@:awa?theme=rule33" alt="loli"/>
</p>
