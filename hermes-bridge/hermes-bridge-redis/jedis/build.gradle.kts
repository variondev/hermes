plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    api(project(":hermes-bridge:hermes-bridge-redis:api"))
}

hermesPublish {
    artifactId = "hermes-bridge-redis-jedis"
}