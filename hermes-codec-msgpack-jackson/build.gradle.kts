plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnly(project(":hermes-common"))
    implementation(project(":hermes-codec-jackson"))
    compileOnly("dev.shiza:dew:1.2.2")
    compileOnly("org.msgpack:jackson-dataformat-msgpack:0.9.8")
}

hermesPublish {
    artifactId = "hermes-codec-msgpack-jackson"
}