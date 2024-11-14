plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnly(project(":hermes-common"))
    compileOnly("dev.shiza:dew:1.2.2")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.18.1")
}

hermesPublish {
    artifactId = "hermes-codec-jackson"
}