plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-bridge-redis"))
}

hermesPublish {
    artifactId = "hermes-bridge-redis-cluster"
}