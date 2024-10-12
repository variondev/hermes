rootProject.name = "hermes"
include(":hermes-common")
include(":hermes-serdes")
include(":hermes-serdes:hermes-serdes-jackson")
include(":hermes-serdes:hermes-serdes-msgpack")
include(":hermes-platforms")
include(":hermes-platforms:hermes-platform-nats")
include(":hermes-platforms:hermes-platform-redis")
include(":hermes-tests")
