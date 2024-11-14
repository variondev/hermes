plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnlyApi(project(":hermes-common"))
    compileOnlyApi("io.lettuce:lettuce-core:6.4.0.RELEASE")
}

hermesPublish {
    artifactId = "hermes-bridge-redis"
}