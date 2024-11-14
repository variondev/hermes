plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
    `hermes-unit`
}

dependencies {
    api("dev.shiza:dew:1.2.2-SNAPSHOT")
    api("com.spotify:completable-futures:0.3.6")
    testImplementation(project(":hermes-codec-msgpack-jackson"))
    testImplementation(project(":hermes-bridge-redis"))
    testImplementation(project(":hermes-bridge-nats"))
    testImplementation("io.lettuce:lettuce-core:6.4.0.RELEASE")
    testImplementation("io.nats:jnats:2.20.2")
}

hermesPublish {
    artifactId = "hermes-common"
}