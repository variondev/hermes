rootProject.name = "hermes"
include(":hermes-common")
include(":hermes-codec")
include(":hermes-codec:hermes-codec-jackson")
include(":hermes-codec:hermes-codec-msgpack")
include(":hermes-bridge")
include(":hermes-bridge:hermes-bridge-nats")
include(":hermes-bridge:hermes-bridge-redis")