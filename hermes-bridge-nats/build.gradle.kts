plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnlyApi(project(":hermes-common"))
    compileOnlyApi("io.nats:jnats:2.20.2")
}

hermesPublish {
    artifactId = "hermes-bridge-nats"
}