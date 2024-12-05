plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnly(project(":hermes-common"))
    compileOnly("dev.shiza:dew:1.2.2")
    compileOnly("io.nats:jnats:2.20.5")
}

hermesPublish {
    artifactId = "hermes-bridge-nats"
}