plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    implementation(project(":hermes-common"))
    implementation("io.nats:jnats:2.20.2")
}

hermesPublish {
    artifactId = "hermes-platform-nats"
}