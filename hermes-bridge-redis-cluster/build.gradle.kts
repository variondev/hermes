plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnly(project(":hermes-common"))
    implementation(project(":hermes-bridge-redis"))
    compileOnly("dev.shiza:dew:1.2.2-SNAPSHOT")
    compileOnly("io.lettuce:lettuce-core:6.4.0.RELEASE")
}

hermesPublish {
    artifactId = "hermes-bridge-redis-cluster"
}