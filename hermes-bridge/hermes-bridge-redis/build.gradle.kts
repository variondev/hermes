plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    subprojects.forEach { implementation(it) }
}

hermesPublish {
    artifactId = "hermes-bridge-redis"
}