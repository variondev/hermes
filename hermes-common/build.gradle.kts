plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
    `hermes-unit`
}

dependencies {
    api("dev.shiza:dew:1.1.2")
    testImplementation(project(":hermes-codec:hermes-codec-msgpack"))
    testImplementation(project(":hermes-bridge:hermes-bridge-redis:lettuce"))
    testImplementation("io.lettuce:lettuce-core:6.4.0.RELEASE")
}

hermesPublish {
    artifactId = "hermes"
}