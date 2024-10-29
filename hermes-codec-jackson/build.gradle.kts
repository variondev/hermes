plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    api(project(":hermes-common"))
    api("com.fasterxml.jackson.core:jackson-databind:2.18.1")
}

hermesPublish {
    artifactId = "hermes-codec-jackson"
}