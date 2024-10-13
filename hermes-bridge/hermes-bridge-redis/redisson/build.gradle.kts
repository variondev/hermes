plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    compileOnlyApi("org.redisson:redisson:3.37.0")
}

hermesPublish {
    artifactId = "hermes-bridge-redis-redisson"
}