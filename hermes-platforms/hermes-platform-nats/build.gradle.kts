plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    api("io.nats:jnats:2.20.2")
}

hermesPublish {
    artifactId = "hermes-platform-nats"
}