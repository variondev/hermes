plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    api("org.msgpack:msgpack-core:0.9.8")
}

hermesPublish {
    artifactId = "hermes-codec-msgpack"
}