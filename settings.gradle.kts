rootProject.name = "hermes"
include(":hermes-common")
include(":hermes-codec")
include(":hermes-codec:hermes-codec-jackson")
include(":hermes-codec:hermes-codec-msgpack")
include(":hermes-platforms")
include(":hermes-platforms:hermes-platform-nats")
include(":hermes-platforms:hermes-platform-redis")