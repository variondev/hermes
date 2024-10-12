plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    api(project(":hermes-bridge:hermes-bridge-redis:api"))
    compileOnlyApi("io.lettuce:lettuce-core:6.4.0.RELEASE")
}

hermesPublish {
    artifactId = "hermes-bridge-redis-lettuce"
}