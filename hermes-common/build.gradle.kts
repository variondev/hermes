plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
    `hermes-unit`
}

dependencies {
    api("dev.shiza:dew:1.1.2")
    testImplementation(project(":hermes-codec:hermes-codec-msgpack"))
    testImplementation(project(":hermes-platforms:hermes-platform-redis"))
}

hermesPublish {
    artifactId = "hermes"
}