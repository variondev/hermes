plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
}

hermesPublish {
    artifactId = "hermes-bridge-redis-api"
}