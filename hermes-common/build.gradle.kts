plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
    `hermes-unit`
}

dependencies {
    implementation("dev.shiza:dew:1.2.2")
    implementation("com.spotify:completable-futures:0.3.6")
    testImplementation(project(":hermes-common"))
    testImplementation(project(":hermes-codec-jackson"))
    testImplementation(project(":hermes-codec-msgpack-jackson"))
    testImplementation(project(":hermes-bridge-redis"))
    testImplementation(project(":hermes-bridge-nats"))
    testImplementation("io.lettuce:lettuce-core:6.4.0.RELEASE")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    testImplementation("org.msgpack:jackson-dataformat-msgpack:0.9.8")
    testImplementation("io.nats:jnats:2.20.2")
}

hermesPublish {
    artifactId = "hermes-common"
}