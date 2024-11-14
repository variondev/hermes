plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-codec-jackson"))
    compileOnlyApi("org.msgpack:jackson-dataformat-msgpack:0.9.8")
}

hermesPublish {
    artifactId = "hermes-codec-msgpack-jackson"
}