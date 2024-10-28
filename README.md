# Hermes

Welcome to **Hermes**, the easy-to-use, flexible, and scalable messaging library designed to make
sending binary data effortless. Whether you're working with popular message brokers or building a
custom solution, Hermes has you covered.

## Getting Started

### Add Repository

To kick things off, add the Hermes repository to your project:

```kotlin
maven("https://repo.araucaris.dev/snapshots")
```

### Add Dependencies

Next, add the necessary Hermes dependencies to your project:

**Core:**

```kotlin
implementation("dev.araucaris:hermes-common:1.0-SNAPSHOT")
```

**Codec:**

```kotlin
implementation("dev.araucaris:hermes-codec-jackson:1.0-SNAPSHOT")
implementation("dev.araucaris:hermes-codec-msgpack:1.0-SNAPSHOT")
```

**Bridge:**

```kotlin
implementation("dev.araucaris:hermes-bridge-nats:1.0-SNAPSHOT")
implementation("dev.araucaris:hermes-bridge-redis:1.0-SNAPSHOT")
```

And that’s it! You’re ready to start crafting with Hermes.

## Troubleshooting

If you encounter an error like:

`> Could not find dev.shiza:dew:1.1.2.`

Just add the following repository:

```kotlin
maven("https://repo.shiza.dev/releases")
```

---

<p align="center">
  <img height="100em" src="https://count.getloli.com/get/@:awa?theme=rule33" alt="loli"/>
</p>