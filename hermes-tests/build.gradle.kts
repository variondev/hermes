plugins {
    `hermes-unit`
    `hermes-repositories`
}

dependencies {
    testImplementation(project(":hermes-common"))
    testImplementation(project(":hermes-serdes:hermes-serdes-msgpack"))
    testImplementation(project(":hermes-platforms:hermes-platform-redis"))
}