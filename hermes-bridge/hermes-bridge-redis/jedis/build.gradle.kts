plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    compileOnlyApi("redis.clients:jedis:5.2.0")
}

hermesPublish {
    artifactId = "hermes-bridge-redis-jedis"
}